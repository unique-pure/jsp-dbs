package team.family.dbs.servlet;

import team.family.dbs.bean.AbsenceRecord;
import team.family.dbs.bean.DormManager;
import team.family.dbs.bean.Student;
import team.family.dbs.service.*;
import team.family.dbs.util.StringUtil;
import team.family.dbs.service.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AbsenceRecordServlet extends HttpServlet {

    AbsenceRecordService absenceRecordService = new AbsenceRecordServiceImpl();
    AcademyService academyService = new AcademyServiceImpl();
    DormService dormService = new DormServiceImpl();
    StudentService studentService = new StudentServiceImpl();
    DormManagerService dormManagerService = new DormManagerServiceImpl();
    /**
     *
     */
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
        String dormBuildId = request.getParameter("buildToSelect");
        String academyToSelect = request.getParameter("academyToSelect");
        String searchType = request.getParameter("searchType");
        String action = request.getParameter("action");

        AbsenceRecord record = new AbsenceRecord();
        if("preSave".equals(action)) {
            recordPreSave(request, response);
            return;
        } else if("save".equals(action)){
            recordSave(request, response);
            return;
        } else if("delete".equals(action)){
            recordDelete(request, response);
            return;
        } else if("list".equals(action)) {
            if(StringUtil.isNotEmpty(s_studentText)) {
                if("name".equals(searchType)) {
                    record.setName(s_studentText);
                } else if("number".equals(searchType)) {
                    record.setStudentId(s_studentText);
                } else if("dorm".equals(searchType)) {
                    record.setRoom_id(s_studentText);
                }else if("academy".equals(searchType)){
                    record.setAcademy(s_studentText);
                }
            }
            if(StringUtil.isNotEmpty(dormBuildId)) {
                record.setDorm_id(Integer.parseInt(dormBuildId));
            }
            if(StringUtil.isNotEmpty(academyToSelect)){
                record.setAcademy(academyToSelect);
            }
            session.removeAttribute("s_studentText");
            session.removeAttribute("searchType");
            session.removeAttribute("buildToSelect");
            session.removeAttribute("academyToSelect");
            request.setAttribute("s_studentText", s_studentText);
            request.setAttribute("searchType", searchType);
            request.setAttribute("buildToSelect", dormBuildId);
            request.setAttribute("academyToSelect",academyToSelect);
        } else if("search".equals(action)){
            if(StringUtil.isNotEmpty(s_studentText)) {
                if("name".equals(searchType)) {
                    record.setName(s_studentText);
                } else if("number".equals(searchType)) {
                    record.setStudentId(s_studentText);
                } else if("dorm".equals(searchType)) {
                    record.setDorm_name(s_studentText);
                }else if("academy".equals(searchType)){
                    record.setAcademy(s_studentText);
                }
                session.setAttribute("s_studentText", s_studentText);
                session.setAttribute("searchType", searchType);
            } else {
                session.removeAttribute("s_studentText");
                session.removeAttribute("searchType");
            }
            if(StringUtil.isNotEmpty(dormBuildId)) {//??????????????????dormBuildId
                record.setDorm_id(Integer.parseInt(dormBuildId));
                session.setAttribute("buildToSelect", dormBuildId);
            }else {
                session.removeAttribute("buildToSelect");
            }
            if(StringUtil.isNotEmpty(academyToSelect)) {//??????????????????academyToSelect
                record.setAcademy(academyToSelect);
                session.setAttribute("academyToSelect", academyToSelect);
            }else {
                session.removeAttribute("academyToSelect");
            }
        }
        if("admin".equals((String)currentUserType)) {//????????????????????????
            System.out.println("??????????????????????????????????????????:" + record);
            List<AbsenceRecord> recordList = absenceRecordService.querryAllRecordCondition(record);
            System.out.println("admin???????????????????????????" + recordList);
            if(recordList !=null){
                System.out.println("admin???????????????????????????" +recordList.size()+"???");
            }
            request.setAttribute("dormBuildList",dormService.queryAllDorm() );
            request.setAttribute("academyList",academyService.querryAllAcademy());
            request.setAttribute("recordList", recordList);
            request.setAttribute("mainPage", "admin/studentRecord.jsp");
            request.getRequestDispatcher("adminIndex.jsp").forward(request, response);
        } else if("dormManager".equals((String)currentUserType)) {
            DormManager manager = (DormManager)(session.getAttribute("currentUser"));
            int buildId = manager.getDorm_id();
            String dorm_name = manager.getDorm_name();
            List<AbsenceRecord> absenceRecords = dormManagerService.queryAllAbsenceRecordByDorm_id(buildId,record);//?????????????????????????????????
            try {
                System.out.println(manager.getUserName() + ":???????????????????????????:" + absenceRecords );
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.setAttribute("dormBuildName", dorm_name);
            request.setAttribute("academyList",academyService.querryAllAcademy());
            request.setAttribute("recordList", absenceRecords);
            request.setAttribute("mainPage", "dormManager/studentRecord.jsp");
            request.getRequestDispatcher("dormManagerIndex.jsp").forward(request, response);

        } else if("student".equals((String)currentUserType)) {
            Student student = (Student)(session.getAttribute("currentUser"));
//            List<AbsenceRecord> recordList = recordDao.recordListWithNumber(con, record, student.getStuNumber());
            List<AbsenceRecord> recordList = absenceRecordService.getAbsenceRecordListByStudentId(student.getStudentId());
            System.out.println("????????????????????????" + recordList);
            request.setAttribute("recordList", recordList);
            request.setAttribute("mainPage", "student/studentRecord.jsp");
            request.getRequestDispatcher("studentIndex.jsp").forward(request, response);
        }
    }

    private void recordDelete(HttpServletRequest request,
                              HttpServletResponse response) {
        String recordId = request.getParameter("recordId");
        absenceRecordService.delAbsenceRecord(Integer.parseInt(recordId));
        try {
            request.getRequestDispatcher("absenceRecord?action=list").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordSave(HttpServletRequest request,
                            HttpServletResponse response)throws ServletException, IOException {
        String recordId = request.getParameter("recordId");
        String studentNumber = request.getParameter("studentNumber");//??????
        String date = request.getParameter("date");
        String detail = request.getParameter("detail");
//        Student destStudent = studentService.isExistsStudent(studentNumber);
        AbsenceRecord record = new AbsenceRecord(studentNumber, date, detail);
        if(StringUtil.isNotEmpty(recordId)) {//?????????????????????
            if(Integer.parseInt(recordId)!=0) {
                record.setRecordId(Integer.parseInt(recordId));
            }
        }

        boolean saveNum = false;
        HttpSession session = request.getSession();
        DormManager manager = (DormManager)(session.getAttribute("currentUser"));
        int buildId = manager.getDorm_id();//??????????????????dorm_id

        Student student = studentService.isExistsStudent(studentNumber);

        if(student.getDorm_id() != manager.getDorm_id()) {
            request.setAttribute("record", record);
            request.setAttribute("error", "????????????????????????????????????");
            request.setAttribute("mainPage", "dormManager/studentRecordSave.jsp");
            request.getRequestDispatcher("dormManagerIndex.jsp").forward(request, response);
        } else {
            record.setDorm_id(student.getDorm_id());
            record.setName(student.getUserName());
            record.setDorm_name(student.getDorm_name());
            if(StringUtil.isNotEmpty(recordId) && Integer.parseInt(recordId)!=0) {//???????????????????????????
                saveNum =  absenceRecordService.updateAbsenceRecord(record);
                if(saveNum){
                    System.out.println("??????????????????!");
                }
            } else {
                saveNum = absenceRecordService.addAbsenceRecord(record);
                if(saveNum){
                    System.out.println("??????" + manager.getUserName() + "???????????????????????????");
                }
            }
            if(saveNum) {
                request.getRequestDispatcher("absenceRecord?action=list").forward(request, response);
            } else {
                request.setAttribute("record", record);
                request.setAttribute("error", "????????????");
                request.setAttribute("mainPage", "dormManager/studentRecordSave.jsp");
                request.getRequestDispatcher("dormManagerIndex.jsp").forward(request, response);
            }
        }

    }

    private void recordPreSave(HttpServletRequest request,
                               HttpServletResponse response)throws ServletException, IOException {
        String recordId = request.getParameter("recordId");
        String studentNumber = request.getParameter("studentNumber");

        if (StringUtil.isNotEmpty(recordId)) {
            AbsenceRecord record = absenceRecordService.getAbsenceRecord(Integer.parseInt(recordId));
            request.setAttribute("record", record);
        } else {
            System.out.println("??????????????????");
            Calendar rightNow = Calendar.getInstance();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            String sysDatetime = fmt.format(rightNow.getTime());
            request.setAttribute("studentNumber", studentNumber);
            request.setAttribute("date", sysDatetime);
        }
        request.setAttribute("mainPage", "dormManager/studentRecordSave.jsp");
        request.getRequestDispatcher("dormManagerIndex.jsp").forward(request, response);
    }
}
