package team.family.dbs.servlet;

import team.family.dbs.service.*;
import team.family.dbs.util.StringUtil;
import team.family.dbs.bean.Academy;
import team.family.dbs.bean.DormManager;
import team.family.dbs.bean.Student;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class StudentServlet extends HttpServlet{
	AdminService adminService = new AdminServiceImpl();
	DormManagerService dormManagerService = new DormManagerServiceImpl();
	StudentService studentService = new StudentServiceImpl();
	DormService dormService = new DormServiceImpl();
	AcademyService academyService = new AcademyServiceImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");

		HttpSession session = request.getSession();
		Object currentUserType = session.getAttribute("currentUserType");
		String s_studentText = request.getParameter("s_studentText");
		String academy1 = request.getParameter("academyToSelect");
		String dormBuildId = request.getParameter("buildToSelect");
		String searchType = request.getParameter("searchType");
		String action = request.getParameter("action");
		Student student = new Student();
		if("preSave".equals(action)) {
			studentPreSave(request, response);
			return;
		} else if("save".equals(action)){
			studentSave(request, response);
			return;
		} else if("delete".equals(action)){
			studentDelete(request, response);
			return;
		} else if("list".equals(action)) {
			if(StringUtil.isNotEmpty(s_studentText)) {
				if("name".equals(searchType)) {
					student.setUserName(s_studentText);
				} else if("number".equals(searchType)) {
					student.setStudentId(s_studentText);
				} else if("dorm".equals(searchType)) {
					student.setDorm_name(s_studentText);
				}
			}
			if(StringUtil.isNotEmpty(dormBuildId)) {
				student.setDorm_id(Integer.parseInt(dormBuildId));
			}
			session.removeAttribute("s_studentText");
			session.removeAttribute("searchType");
			session.removeAttribute("buildToSelect");
			request.setAttribute("s_studentText", s_studentText);
			request.setAttribute("searchType", searchType);
			request.setAttribute("buildToSelect", dormBuildId);
		} else if("search".equals(action)){
			if(StringUtil.isNotEmpty(s_studentText)) {
				if("name".equals(searchType)) {//??????????????????????????????
					student.setUserName(s_studentText);
				} else if("number".equals(searchType)) {//??????????????????????????????
					student.setStudentId(s_studentText);
				} else if("dorm".equals(searchType)) {//????????????????????????????????????
					student.setDorm_name(s_studentText);
				}else if("academy".equals(searchType)){
					student.setAcademy(s_studentText);
				}
				session.setAttribute("s_studentText", s_studentText);
				session.setAttribute("searchType", searchType);
			} else {
				session.removeAttribute("s_studentText");
				session.removeAttribute("searchType");
			}
			if(StringUtil.isNotEmpty(dormBuildId)) {
				student.setDorm_id(Integer.parseInt(dormBuildId));
				session.setAttribute("buildToSelect", dormBuildId);
			}else {
				session.removeAttribute("buildToSelect");
			}
			if(StringUtil.isNotEmpty(academy1)) {
				student.setAcademy(academy1);
				session.setAttribute("academyToSelect", academy1);
			}else {
				session.removeAttribute("academyToSelect");
			}
		} else {
			if("admin".equals((String)currentUserType)) {
				if(StringUtil.isNotEmpty(s_studentText)) {
					if("name".equals(searchType)) {
						student.setUserName(s_studentText);
					} else if("number".equals(searchType)) {
						student.setStudentId(s_studentText);
					} else if("dorm".equals(searchType)) {
						student.setDorm_name(s_studentText);
					}
					session.setAttribute("s_studentText", s_studentText);
					session.setAttribute("searchType", searchType);
				}
				if(StringUtil.isNotEmpty(dormBuildId)) {
					student.setDorm_id(Integer.parseInt(dormBuildId));
					session.setAttribute("buildToSelect", dormBuildId);
				}
				if(StringUtil.isEmpty(s_studentText) && StringUtil.isEmpty(dormBuildId)) {
					Object o1 = session.getAttribute("s_studentText");
					Object o2 = session.getAttribute("searchType");
					Object o3 = session.getAttribute("buildToSelect");
					if(o1!=null) {
						if("name".equals((String)o2)) {
							student.setUserName((String)o1);
						} else if("number".equals((String)o2)) {
							student.setStudentId((String)o1);
						} else if("dorm".equals((String)o2)) {
							student.setDorm_name((String)o1);
						}
					}
					if(o3 != null) {
						student.setDorm_id(Integer.parseInt((String)o3));
					}
				}
			} else if("dormManager".equals((String)currentUserType)) {
				if(StringUtil.isNotEmpty(s_studentText)) {
					if("name".equals(searchType)) {
						student.setUserName(s_studentText);
					} else if("number".equals(searchType)) {
						student.setStudentId(s_studentText);
					} else if("dorm".equals(searchType)) {
						student.setDorm_name(s_studentText);
					}
					session.setAttribute("s_studentText", s_studentText);
					session.setAttribute("searchType", searchType);
				}
				if(StringUtil.isEmpty(s_studentText)) {
					Object o1 = session.getAttribute("s_studentText");
					Object o2 = session.getAttribute("searchType");
					if(o1!=null) {
						if("name".equals((String)o2)) {
							student.setUserName((String)o1);
						} else if("number".equals((String)o2)) {
							student.setStudentId((String)o1);
						} else if("dorm".equals((String)o2)) {
							student.setDorm_name((String)o1);
						}
					}
				}
			}
		}

		if("admin".equals((String)currentUserType)) {//??????????????????????????????
			List<Student> studentList = studentService.queryAllStudentByCondition(student);
			List<Academy> academyList = academyService.querryAllAcademy();
			session.setAttribute("academyList",academyList);
			session.setAttribute("dormBuildList", dormService.queryAllDorm());
			request.setAttribute("studentList", studentList);
			request.setAttribute("mainPage", "admin/studentOperation.jsp");
			request.getRequestDispatcher("adminIndex.jsp").forward(request, response);
		} else if("dormManager".equals((String)currentUserType)) {
			DormManager manager = (DormManager)(session.getAttribute("currentUser"));
			List<Academy> academyList = academyService.querryAllAcademy();
			List<Student> studentList = studentService.queryAllStudentByCondition(student);
			session.setAttribute("academyList",academyList);
			session.setAttribute("dormBuildList", dormService.queryAllDorm());
			int buildId = manager.getDorm_id();
			String buildName = manager.getDorm_name();
//			List<Student> studentList = studentDao.studentListWithBuild(con, student, buildId);
			request.setAttribute("dormBuildName", buildName);
			request.setAttribute("studentList", studentList);
			request.setAttribute("mainPage", "dormManager/studentOperation.jsp");
			request.getRequestDispatcher("dormManagerIndex.jsp").forward(request, response);
		}
	}

	private void studentDelete(HttpServletRequest request,
			HttpServletResponse response) {
		String studentId = request.getParameter("studentId");
		if(studentService.delStudentByStudentId(studentId)){
			System.out.println("??????????????????!");
		}else{
			System.out.println("??????????????????");
		}
		try {
			request.getRequestDispatcher("student?action=list").forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void studentSave(HttpServletRequest request,
			HttpServletResponse response)throws ServletException, IOException {
		String studentId = request.getParameter("studentId");//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????studentId
		String studentIdNew = request.getParameter("studentIdNew");
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String sex = request.getParameter("sex");
		String tel = request.getParameter("tel");
//		String dorm_name = request.getParameter("dorm_name");
		String native_place = request.getParameter("native_place");
		String academy = request.getParameter("academy");
		String major_and_class = request.getParameter("major_and_class");
		int dorm_id = Integer.parseInt(request.getParameter("dormBuildId"));
//		if(dorm_name != "" && dorm_name !=null){
//			dorm_id = dormService.selectDorm_idByDorm_Name(dorm_name);
//		}else{
//			System.out.println("?????????id???????????????????????????");
//		}
		String dorm_name  = dormService.selectDorm_nameByDorm_id(dorm_id);
		String room_id = request.getParameter("room_id");
//		String dorm_name = dormService.selectDorm_nameByDorm_id(dorm_id);
		Student student = new Student(studentId,userName, password, sex, tel, native_place, academy, major_and_class,dorm_id,dorm_name,room_id,0);
		Student studentNew = new Student(studentIdNew,userName, password, sex, tel, native_place, academy, major_and_class,dorm_id,dorm_name,room_id,0);
		if(StringUtil.isNotEmpty(studentId)) {
			student.setStudentId(studentId);
		}
		boolean save = false;

		if(StringUtil.isNotEmpty(studentId)) {//????????????????????????????????????????????????
			System.out.println("???????????????????????????");
			save = studentService.updateStudentByStudentId(student);
		} else if(studentService.isExistsStudent(studentIdNew,password) != null){//??????????????????????????? ??????????????????????????????????????????!!!??????????????????????????????save?????????????????????false??????????????????!
			request.setAttribute("student", student);
			request.setAttribute("error", "??????????????????");
			request.setAttribute("mainPage", "admin/studentOperationSave.jsp");
			request.getRequestDispatcher("adminIndex.jsp").forward(request, response);
			return;
		} else {
			System.out.println("???????????????????????????");
			save = studentService.addStudent(studentNew);
			if(save){
				System.out.println("??????????????????!");
			}
		}
		if(save) {
			request.getRequestDispatcher("student?action=list").forward(request, response);
		} else {
			request.setAttribute("student", student);
			request.setAttribute("error", "????????????");
			request.setAttribute("mainPage", "admin/studentOperationSave.jsp");
			request.getRequestDispatcher("adminIndex.jsp").forward(request, response);
		}
	}

	private void studentPreSave(HttpServletRequest request,
			HttpServletResponse response)throws ServletException, IOException {


//		HttpSession session = request.getSession();
//		List<Academy> academyList = (List<Academy>)session.getAttribute("academyList");
//
//		List<Dorm> dormBuildList = (List<Dorm>)session.getAttribute("dormBuildList") ;
//		System.out.println(dormBuildList + "????????????????");


		String studentId = request.getParameter("studentId");
		if (StringUtil.isNotEmpty(studentId)) {
			Student student = studentService.isExistsStudent(studentId);
			System.out.println("???????????????????????????????????????:" + student);
			request.setAttribute("student", student);
			List<Academy> academies = academyService.querryAllAcademy();
			request.setAttribute("academyList",academies);
			request.setAttribute("dormBuildList",dormService.queryAllDorm());
		}
		request.setAttribute("mainPage", "admin/studentOperationSave.jsp");
		request.getRequestDispatcher("adminIndex.jsp").forward(request, response);
	}

	
}
