import sys
import json
from ortools.sat.python import cp_model

def parse_time(time_str):
    """HH:MM 또는 HH:MM:SS 형식의 시간을 분으로 안전하게 변환"""
    if not time_str or ':' not in time_str:
        return -1
    try:
        parts = time_str.split(':')
        h = int(parts[0])
        m = int(parts[1])
        return h * 60 + m
    except (ValueError, TypeError, IndexError):
        return -1

class TimeTableCPSATSolver:
    def __init__(self, data):
        print("--- [1] Initializing Solver ---", file=sys.stderr)
        self.data = data
        self.model = cp_model.CpModel()
        self.solver = cp_model.CpSolver()

        if 'lectures' not in data or 'courses' not in data or 'user_input' not in data:
            raise ValueError("Input JSON is missing required keys.")

        self.lectures = [lec for lec in data['lectures'] if lec.get('courseId')]
        self.courses = {c['id']: c for c in data['courses']}
        self.user_input = data['user_input']
        self.preferences = self.user_input.get('preferences') or []

        self.num_lectures = len(self.lectures)
        self.selected_lectures = [self.model.NewBoolVar(f'selected_{i}') for i in range(self.num_lectures)]

        print(f"- Total lectures to consider: {self.num_lectures}", file=sys.stderr)
        print(f"- User preferences: {self.preferences}", file=sys.stderr)

        # 가중치 설정
        self.job_recommendation_weight = 50
        self.holiday_penalty = 30
        self.lunch_break_penalty = 30
        self.avoid_morning_penalty_early = 20
        self.avoid_morning_penalty_late = 10
        self.avoid_evening_penalty = 20
        self.course_count_penalty = 5000

    def solve(self):
        self._add_constraints()
        self._define_objective()

        print("\n--- [4] Solving Model ---", file=sys.stderr)
        status = self.solver.Solve(self.model)

        status_name = self.solver.StatusName(status)
        print(f"- Solver status: {status_name}", file=sys.stderr)

        if status in (cp_model.OPTIMAL, cp_model.FEASIBLE):
            return self._get_solution()
        else:
            print("- No solution found.", file=sys.stderr)
            return {"status": "FAILED", "message": "해당 조건에 맞는 시간표를 생성할 수 없습니다. 조건을 변경하여 다시 시도해주세요.", "lectureIds": []}

    def _add_constraints(self):
        print("\n--- [2] Adding Constraints ---", file=sys.stderr)

        required_ids = set(self.user_input.get('includeCourses') or [])
        excluded_ids = set(self.user_input.get('excludeCourses') or [])

        courses_to_lectures = {}
        for i, lec in enumerate(self.lectures):
            course_id = lec['courseId']
            if course_id not in courses_to_lectures:
                courses_to_lectures[course_id] = []
            courses_to_lectures[course_id].append(self.selected_lectures[i])

        for course_id, lectures in courses_to_lectures.items():
            if len(lectures) > 1: self.model.Add(sum(lectures) <= 1)
            if course_id in required_ids: self.model.Add(sum(lectures) >= 1)
            if course_id in excluded_ids:
                for lec_var in lectures: self.model.Add(lec_var == 0)

        for i in range(self.num_lectures):
            for j in range(i + 1, self.num_lectures):
                lec1, lec2 = self.lectures[i], self.lectures[j]
                is_overlapping = False
                for t1 in lec1.get('times') or []:
                    start1, end1 = parse_time(t1.get('startTime')), parse_time(t1.get('endTime'))
                    if start1 == -1: continue
                    for t2 in lec2.get('times') or []:
                        start2, end2 = parse_time(t2.get('startTime')), parse_time(t2.get('endTime'))
                        if start2 != -1 and t1.get('day') == t2.get('day') and max(start1, start2) < min(end1, end2):
                            is_overlapping = True; break
                    if is_overlapping: break
                if is_overlapping:
                    self.model.AddBoolOr([self.selected_lectures[i].Not(), self.selected_lectures[j].Not()])
        
        target_credits = self.user_input.get('targetCredits')
        if target_credits:
            print(f"- Applying credit constraints: {target_credits}", file=sys.stderr)
            for ctype, required_credit in target_credits.items():
                if required_credit > 0:
                    actual_credits_expr = sum(
                        self.selected_lectures[i] * self.courses[lec['courseId']]['credit']
                        for i, lec in enumerate(self.lectures) if self.courses[lec['courseId']].get('ctype') == ctype
                    )
                    # 지정된 학점과 정확히 일치하도록 제약조건 추가
                    self.model.Add(actual_credits_expr == required_credit)
                    print(f"- Constraint added: {ctype} credits must be exactly {required_credit}", file=sys.stderr)


    def _define_objective(self):
        print("\n--- [3] Defining Objective Function ---", file=sys.stderr)

        total_job_score = 0
        course_scores = self.data.get('course_scores', {})
        if course_scores:
            job_scores = [course_scores.get(lec['courseId'], 0) for lec in self.lectures]
            total_job_score = sum(self.selected_lectures[i] * job_scores[i] for i in range(self.num_lectures))

        preference_penalty = self._calculate_preference_penalty()

        # 선택된 강의의 총 수에 비례하는 페널티를 추가하여, 더 적은 과목을 선호하도록 유도
        num_selected_courses = sum(self.selected_lectures)
        course_count_penalty_term = num_selected_courses * self.course_count_penalty
        print(f"- Applying course count penalty. Weight: {self.course_count_penalty}", file=sys.stderr)


        # 총 페널티 = 선호도 페널티 + 과목 수 페널티
        total_penalty = preference_penalty + course_count_penalty_term
        
        # 목적 함수: 직업군 점수 최대화, 페널티 최소화
        self.model.Maximize(total_job_score * self.job_recommendation_weight - total_penalty)
        print("- Objective function defined (Maximize Score, Minimize Penalties).", file=sys.stderr)


    def _calculate_preference_penalty(self):
        all_penalty_terms = []

        if "prefer_holidays" in self.preferences:
            days = ["월", "화", "수", "목", "금"]
            for day in days:
                lectures_on_day_vars = [self.selected_lectures[i] for i, lec in enumerate(self.lectures) if any(t.get('day') == day for t in (lec.get('times') or []))]
                if lectures_on_day_vars:
                    day_is_active = self.model.NewBoolVar(f'day_active_{day}')
                    self.model.Add(sum(lectures_on_day_vars) > 0).OnlyEnforceIf(day_is_active)
                    self.model.Add(sum(lectures_on_day_vars) == 0).OnlyEnforceIf(day_is_active.Not())
                    all_penalty_terms.append(day_is_active * self.holiday_penalty)

        for i, lec in enumerate(self.lectures):
            lecture_penalties = {}
            earliest_start = 9999

            for time in lec.get('times') or []:
                start = parse_time(time.get('startTime'))
                if start == -1: continue
                earliest_start = min(earliest_start, start)

                if "lunch_break" in self.preferences and max(start, 720) < min(parse_time(time.get('endTime')), 780):
                    lecture_penalties['lunch'] = self.lunch_break_penalty

            if earliest_start != 9999:
                if "avoid_morning" in self.preferences:
                    if earliest_start < 600: lecture_penalties['morning'] = self.avoid_morning_penalty_early
                    elif earliest_start < 720: lecture_penalties['morning'] = self.avoid_morning_penalty_late
                if "avoid_evening" in self.preferences and earliest_start >= 1020:
                    lecture_penalties['evening'] = self.avoid_evening_penalty

            for penalty in lecture_penalties.values():
                all_penalty_terms.append(self.selected_lectures[i] * penalty)

        return sum(all_penalty_terms) if all_penalty_terms else 0

    def _get_solution(self):
        selected_indices = [i for i, var in enumerate(self.selected_lectures) if self.solver.Value(var)]
        if not selected_indices:
            return {"status": "FAILED", "message": "추천할 강의 조합을 찾지 못했습니다.", "lectureIds": []}

        result_lecture_ids = [self.lectures[i]['id'] for i in selected_indices]
        print(f"\n--- [5] Generating Solution: Found {len(selected_indices)} lectures. ---", file=sys.stderr)
        return {"status": "SUCCESS", "lectureIds": result_lecture_ids}

if __name__ == "__main__":
    try:
        input_json = sys.stdin.read()
        if not input_json: raise ValueError("Input JSON from Java is empty.")
        data = json.loads(input_json)

        solver = TimeTableCPSATSolver(data)
        result = solver.solve()

        print(json.dumps(result))

    except Exception as e:
        error_message = f"An unexpected error occurred in optimizer.py: {type(e).__name__} - {e}"
        print(json.dumps({"status": "ERROR", "message": error_message, "lectureIds": []}))
        print(error_message, file=sys.stderr)
