import sys
import json
from ortools.sat.python import cp_model

def parse_time(time_str):
    """HH:MM 형식의 시간을 분으로 변환"""
    h, m = map(int, time_str.split(':'))
    return h * 60 + m

def format_time(minutes):
    """분을 HH:MM 형식의 시간으로 변환"""
    h = minutes // 60
    m = minutes % 60
    return f"{h:02d}:{m:02d}"

class TimeTableCPSATSolver:
    def __init__(self, data):
        print("--- [1] Initializing Solver ---", file=sys.stderr)
        self.data = data
        self.model = cp_model.CpModel()
        self.solver = cp_model.CpSolver()
        
        # 'course_id'가 없는 강의 데이터 필터링
        original_lecture_count = len(data['lectures'])
        self.lectures = [lec for lec in data['lectures'] if 'course_id' in lec and lec.get('course_id') is not None]
        filtered_out_count = original_lecture_count - len(self.lectures)
        if filtered_out_count > 0:
            print(f"- Filtered out {filtered_out_count} lectures with missing 'course_id'.", file=sys.stderr)

        self.courses = {c['id']: c for c in data['courses']}
        self.user_input = data['user_input']
        preferences = self.user_input.get('preferences')
        self.preferences = preferences if preferences is not None else []

        self.num_lectures = len(self.lectures)
        self.selected_lectures = [self.model.NewBoolVar(f'selected_{i}') for i in range(self.num_lectures)]
        
        print(f"- Total lectures to consider: {self.num_lectures}", file=sys.stderr)
        print(f"- User preferences: {self.preferences}", file=sys.stderr)

        # 최적화 목표와 페널티/보상 가중치
        self.total_penalties = {}
        self.credit_maximization_weight = 5000
        self.job_recommendation_weight = 1000
        self.holiday_penalty = 50
        self.lunch_break_penalty = 30
        self.avoid_morning_penalty_early = 20
        self.avoid_morning_penalty_late = 10
        self.avoid_evening_penalty = 15
        self.gap_penalty = 5
        self.consecutive_lecture_penalty = 20

    def solve(self):
        self._add_constraints()
        self._define_objective()

        print("\n--- [4] Solving Model ---", file=sys.stderr)
        status = self.solver.Solve(self.model)
        
        status_name = self.solver.StatusName(status)
        print(f"- Solver status: {status_name}", file=sys.stderr)
        print(f"- Solver response stats: {self.solver.ResponseStats()}", file=sys.stderr)

        if status == cp_model.OPTIMAL or status == cp_model.FEASIBLE:
            return self._get_solution()
        else:
            print("- No solution found.", file=sys.stderr)
            return {"lecture_ids": []}

    def _add_constraints(self):
        print("\n--- [2] Adding Constraints ---", file=sys.stderr)
        # 1. 필수 과목 제약
        required_course_ids = self.user_input.get('includeCourses')
        if required_course_ids:
            print(f"- Adding required courses constraint for IDs: {required_course_ids}", file=sys.stderr)
            for course_id in required_course_ids:
                lectures_for_course = [i for i, lec in enumerate(self.lectures) if self.courses[lec['course_id']]['class_id'] == course_id]
                if lectures_for_course:
                    self.model.Add(sum(self.selected_lectures[i] for i in lectures_for_course) >= 1)

        # 2. 제외 과목 제약
        exclude_course_ids = self.user_input.get('excludeCourses')
        if exclude_course_ids:
            print(f"- Adding excluded courses constraint for IDs: {exclude_course_ids}", file=sys.stderr)
            for course_id in exclude_course_ids:
                lectures_for_course = [i for i, lec in enumerate(self.lectures) if self.courses[lec['course_id']]['class_id'] == course_id]
                for i in lectures_for_course:
                    self.model.Add(self.selected_lectures[i] == 0)

        # 3. 시간 충돌 제약
        print("- Adding time conflict constraints.", file=sys.stderr)
        for i in range(self.num_lectures):
            for j in range(i + 1, self.num_lectures):
                lec1 = self.lectures[i]
                lec2 = self.lectures[j]
                
                is_overlapping = False
                for t1 in lec1.get('lecture_time', []):
                    for t2 in lec2.get('lecture_time', []):
                        if t1['day'] == t2['day']:
                            start1, end1 = parse_time(t1['start_time']), parse_time(t1['end_time'])
                            start2, end2 = parse_time(t2['start_time']), parse_time(t2['end_time'])
                            if max(start1, start2) < min(end1, end2):
                                is_overlapping = True
                                break
                    if is_overlapping:
                        break
                
                if is_overlapping:
                    self.model.AddBoolOr([self.selected_lectures[i].Not(), self.selected_lectures[j].Not()])

        # 4. 동일 과목 중복 수강 방지 제약
        print("- Adding same course duplicate prevention constraints.", file=sys.stderr)
        courses_to_lectures = {}
        for i, lec in enumerate(self.lectures):
            course_id = self.courses[lec['course_id']]['class_id']
            if course_id not in courses_to_lectures:
                courses_to_lectures[course_id] = []
            courses_to_lectures[course_id].append(i)

        for course_id, lecture_indices in courses_to_lectures.items():
            if len(lecture_indices) > 1:
                self.model.Add(sum(self.selected_lectures[i] for i in lecture_indices) <= 1)

    def _define_objective(self):
        print("\n--- [3] Defining Objective Function ---", file=sys.stderr)
        # 1. 총 학점 계산
        credits = [self.courses[lec['course_id']]['credit'] for lec in self.lectures]
        total_credits = sum(self.selected_lectures[i] * credits[i] for i in range(self.num_lectures))
        print(f"- Defined total credits term (weight: {self.credit_maximization_weight})", file=sys.stderr)

        # 2. 직업군 추천 점수 계산
        job_group = self.user_input.get('jobGroup')
        total_job_score = 0
        if job_group:
            job_scores = []
            for i, lec in enumerate(self.lectures):
                course = self.courses[lec['course_id']]
                score = course.get('jobRecommendations', {}).get(job_group, 0)
                job_scores.append(score)
            total_job_score = sum(self.selected_lectures[i] * job_scores[i] for i in range(self.num_lectures))
            print(f"- Defined job recommendation score term for '{job_group}' (weight: {self.job_recommendation_weight})", file=sys.stderr)

        # 3. 페널티 계산
        self._calculate_penalties()
        
        # 최종 목표 함수 설정
        self.model.Maximize(
            total_credits * self.credit_maximization_weight +
            total_job_score * self.job_recommendation_weight -
            sum(self.total_penalties.values())
        )
        print("- Objective function defined: (Credits * Weight) + (JobScore * Weight) - TotalPenalties", file=sys.stderr)

    def _calculate_penalties(self):
        print("- Calculating penalties based on preferences:", file=sys.stderr)
        days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"]
        
        day_vars = {day: self.model.NewBoolVar(f'day_{day}') for day in days}
        for day in days:
            lectures_on_day = [self.selected_lectures[i] for i, lec in enumerate(self.lectures) if any(t['day'] == day for t in lec.get('lecture_time', []))]
            if lectures_on_day:
                self.model.Add(day_vars[day] == 1).OnlyEnforceIf(cp_model.LinearExpr.Sum(lectures_on_day) > 0)
                self.model.Add(day_vars[day] == 0).OnlyEnforceIf(cp_model.LinearExpr.Sum(lectures_on_day) == 0)
            else:
                self.model.Add(day_vars[day] == 0)

        if "prefer_holidays" in self.preferences:
            total_school_days = sum(day_vars[day] for day in days)
            self.total_penalties['holiday_penalty'] = total_school_days * self.holiday_penalty
            print(f"  - Penalty for school days (prefer_holidays): {self.holiday_penalty} per day", file=sys.stderr)

        if "lunch_break" in self.preferences:
            lunch_start, lunch_end = parse_time("12:00"), parse_time("13:00")
            lunch_penalties = []
            for i in range(self.num_lectures):
                for time in self.lectures[i].get('lecture_time', []):
                    lec_start, lec_end = parse_time(time['start_time']), parse_time(time['end_time'])
                    if max(lec_start, lunch_start) < min(lec_end, lunch_end):
                        lunch_penalties.append(self.selected_lectures[i] * self.lunch_break_penalty)
                        break
            self.total_penalties['lunch_break_penalty'] = sum(lunch_penalties)
            print(f"  - Penalty for lectures during lunch time (12:00-13:00): {self.lunch_break_penalty} per lecture", file=sys.stderr)
        
        if "avoid_morning" in self.preferences:
            early_morning_start, early_morning_end = parse_time("08:00"), parse_time("10:00")
            late_morning_start, late_morning_end = parse_time("10:00"), parse_time("12:00")
            morning_penalties = []
            for i in range(self.num_lectures):
                for time in self.lectures[i].get('lecture_time', []):
                    lec_start = parse_time(time['start_time'])
                    if early_morning_start <= lec_start < early_morning_end:
                        morning_penalties.append(self.selected_lectures[i] * self.avoid_morning_penalty_early)
                    elif late_morning_start <= lec_start < late_morning_end:
                        morning_penalties.append(self.selected_lectures[i] * self.avoid_morning_penalty_late)
            self.total_penalties['avoid_morning_penalty'] = sum(morning_penalties)
            print(f"  - Penalty for morning lectures (08:00-10:00: {self.avoid_morning_penalty_early}, 10:00-12:00: {self.avoid_morning_penalty_late})", file=sys.stderr)

        if "avoid_evening" in self.preferences:
            evening_start = parse_time("17:00")
            evening_penalties = []
            for i in range(self.num_lectures):
                for time in self.lectures[i].get('lecture_time', []):
                    if parse_time(time['start_time']) >= evening_start:
                        evening_penalties.append(self.selected_lectures[i] * self.avoid_evening_penalty)
                        break
            self.total_penalties['avoid_evening_penalty'] = sum(evening_penalties)
            print(f"  - Penalty for evening lectures (17:00 onwards): {self.avoid_evening_penalty} per lecture", file=sys.stderr)

    def _get_solution(self):
        print("\n--- [5] Generating Solution ---", file=sys.stderr)
        selected_indices = [i for i, var in enumerate(self.selected_lectures) if self.solver.Value(var)]
        
        if not selected_indices:
            print("- Solver found a feasible solution, but it contains no lectures.", file=sys.stderr)
            print(f"- Final Objective value: {self.solver.ObjectiveValue()}", file=sys.stderr)
            return {"lecture_ids": []}
            
        result_lecture_ids = [self.lectures[i]['id'] for i in selected_indices]
        
        print(f"- Found {len(selected_indices)} lectures for the timetable.", file=sys.stderr)
        print(f"- Final Objective value: {self.solver.ObjectiveValue()}", file=sys.stderr)

        # Detailed breakdown of the final score
        final_credits = sum(self.courses[self.lectures[i]['course_id']]['credit'] for i in selected_indices)
        job_group = self.user_input.get('jobGroup')
        final_job_score = 0
        if job_group:
            final_job_score = sum(self.courses[self.lectures[i]['course_id']].get('jobRecommendations', {}).get(job_group, 0) for i in selected_indices)
        
        print("\n--- Final Score Breakdown ---", file=sys.stderr)
        print(f"- Total Credits: {final_credits} -> Score: {final_credits * self.credit_maximization_weight}", file=sys.stderr)
        print(f"- Total Job Score: {final_job_score} -> Score: {final_job_score * self.job_recommendation_weight}", file=sys.stderr)

        print("\n--- Selected Lectures ---", file=sys.stderr)
        for i in selected_indices:
            lec = self.lectures[i]
            course = self.courses[lec['course_id']]
            job_score = course.get('jobRecommendations', {}).get(job_group, 0) if job_group else 0
            times = ", ".join([f"{t['day']} {t['start_time']}-{t['end_time']}" for t in lec.get('lecture_time', [])])
            print(f"  - Lecture ID: {lec['id']}, Course: {course['name']}({course['class_id']}), Credit: {course['credit']}, Job Score: {job_score}, Times: {times}", file=sys.stderr)

        return {"lecture_ids": result_lecture_ids}


if __name__ == "__main__":
    try:
        input_json = sys.stdin.read()
        data = json.loads(input_json)
        
        solver = TimeTableCPSATSolver(data)
        result = solver.solve()
        
        print(json.dumps(result))
    except Exception as e:
        print(f"An error occurred: {e}", file=sys.stderr)
        # 에러 발생 시에도 빈 결과를 출력하여 Java에서 JSON 파싱 오류를 방지
        print(json.dumps({"lecture_ids": [], "error": str(e)}))
