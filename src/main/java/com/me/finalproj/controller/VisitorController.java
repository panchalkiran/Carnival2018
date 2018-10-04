package com.me.finalproj.controller;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.me.finalproj.dao.EventDAO;
import com.me.finalproj.dao.VisitorDAO;
import com.me.finalproj.exception.FERSGenericException;
import com.me.finalproj.pojo.Email;
import com.me.finalproj.pojo.Event;
import com.me.finalproj.pojo.Visitor;

@Controller
@RequestMapping("/visitor/*")
public class VisitorController {

	@Autowired
	@Qualifier("visitorDao")
	VisitorDAO vDAO;

	@Autowired
	@Qualifier("eventDao")
	EventDAO eDAO;

	private static Logger log = Logger.getLogger(VisitorController.class);

	@RequestMapping(value = "/visitor/register.htm")
	public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("registration");
	}

	@RequestMapping("/visitor/newVistor.htm")
	public ModelAndView newVisitor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request == null || response == null) {
			log.info("Request or Response failed for NEWVISITOR METHOD..");
			throw new FERSGenericException(
					"Error in Transaction, Please re-Try. for more information check Logfile in C:\\FERSLOG folder",
					new NullPointerException());
		}
		String username = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");
		String firstname = request.getParameter("FIRSTNAME");
		String lastname = request.getParameter("LASTNAME");
		String email = request.getParameter("EMAIL");
		String phoneno = request.getParameter("PHONENO");
		String place = request.getParameter("ADDRESS");

		log.info("creating new visitor with UserName :" + username);

		Visitor visitor = new Visitor();
		Email em = new Email();
		em.setEmailAddress(email);
		if ((username.equals("admin")) && (password.equals("admin"))) {
			visitor.setAdmin(true);
		} else {
			visitor.setAdmin(false);
		}

		visitor.setUserName(username);
		visitor.setPassword(password);
		visitor.setFirstName(firstname);
		visitor.setLastName(lastname);
		visitor.setAddress(place);
		visitor.setPhoneNumber(phoneno);
		visitor.setEmail(em);
		em.setVisit(visitor);

		boolean insertStatus = vDAO.insertVisitor(visitor);

		ModelAndView mv = new ModelAndView();
		if (insertStatus == true) {
			mv.addObject("REGISTRATIONSTATUSMESSAGE", "User Registered Succesfully !!!");
			log.info("Succesfully created visitor " + username);
			mv.setViewName("registration");
		} else {
			mv.addObject("REGISTRATIONSTATUSMESSAGE",
					"USERNAME already exists.. please register again with different USERNAME..");
			log.info("Username " + username + " already exists and visitor creation failed..");
			mv.setViewName("registration");
		}
		return mv;
	}

	@RequestMapping("/visitor/searchVisitor.htm")
	public ModelAndView searchVisitor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (request == null || response == null) {
			log.info("Request or Response failed for SEARCHVISITOR METHOD..");
			throw new FERSGenericException(
					"Error in Transaction, Please re-Try. for more information check Logfile in C:\\FERSLOG folder",
					new NullPointerException());
		}
		HttpSession session = (HttpSession) request.getSession();
		String username = request.getParameter("USERNAME");
		String password = request.getParameter("PASSWORD");

		session.setAttribute("USERNAME", username);
		session.setAttribute("PASSWORD", password);

		log.info("Logging into FERS using username :" + username + " and password :" + password);

		Visitor visitor = new Visitor();
		visitor = vDAO.searchVisitor(username, password);

		ModelAndView mv = new ModelAndView();

		List<Event> eventList = new ArrayList<Event>();
		eventList = eDAO.showAllEvents();
		if (visitor == null) {
			mv.addObject("ERROR", "Invalid Username / Password.");
			mv.setViewName("index");
			return mv;
		} else if (visitor.isAdmin()) {
			mv.addObject("visitor", visitor);
			mv.addObject("allEvents", eventList);
			mv.setViewName("adminmain");

			return mv;
		} else {

			log.info("Visitor details available for the username :" + username);
			System.out.println("User Authenticated succeessfuuhiui");
			log.info("All events listed for the visitor :" + eventList);

			session.setAttribute("SessionVisitor", visitor);
			mv.addObject("visitor", visitor);
			mv.addObject("allEvents", eventList);

			mv.setViewName("visitormain");
			return mv;
		}
	}

	@RequestMapping(value = "/visitor/logout.htm")
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession hs = request.getSession();
		hs.invalidate();
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping(value = "/visitor/generatePdf.htm")
	public ModelAndView changePwd(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String id = request.getParameter("vid");
		Visitor v = vDAO.getVisitorById(id);

		Set<Event> eList = v.getRegisteredEvents();
		Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\kiran\\Desktop\\event.pdf"));
        
        document.open();
        document.newPage();
        document.add(new Paragraph("Visitor Name: " + v.getFirstName() + " " + v.getLastName())); 
		document.add(new Paragraph("Phone Number: " + v.getPhoneNumber()));
		document.add(new Paragraph("Email Address: " + v.getEmail().getEmailAddress()));
		document.add(new Paragraph("Registered Events are: " ));
		document.add(new Paragraph());
		document.add(new Paragraph("Event Name  "  + " Event Type  "  + " Event Place " ));
		for(Event e: eList)
			document.add(new Paragraph(e.getName() + "		  " + e.getEventtype() + " 		 " + e.getPlace()));
        document.close();
        ModelAndView mv = new ModelAndView();
        mv.addObject("visitor", v);
		mv.addObject("allEvents", eList);
		mv.addObject("Error", "PDF Generated Successfully!");
		mv.setViewName("visitormain");
        return mv;

	}

	@RequestMapping(value = "/visitor/index.htm")
	public ModelAndView indexPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("index");
	}
}
