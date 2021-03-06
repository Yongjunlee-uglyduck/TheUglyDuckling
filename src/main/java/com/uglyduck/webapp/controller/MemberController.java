package com.uglyduck.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uglyduck.command.member.MemberActivityCommand;
import com.uglyduck.command.member.MemberCommand;
import com.uglyduck.command.member.MemberCookieSaveCommand;
import com.uglyduck.command.member.MemberDropCommand;
import com.uglyduck.command.member.MemberJoinCommand;
import com.uglyduck.command.member.MemberNewPasswordCommand;
import com.uglyduck.command.member.MemberPwUpdateCommand;
import com.uglyduck.command.member.MemberUpdateCommand;
import com.uglyduck.webapp.dao.MemberDao;
import com.uglyduck.webapp.dto.MemberDto;

@Controller
public class MemberController {
	
	// Field
	@Autowired
	SqlSession sqlSession;
	MemberCommand memberCommand;
	
	// Method
	@RequestMapping("login-form")
	public String loginForm() {
		return "login/loginPage";
	}
	
	@RequestMapping("sign-form")
	public String signForm(Model model) {
		model.addAttribute("memberDto", new MemberDto());
		return "join/joinPage";
	}
	
	@RequestMapping("login")
	public String loginUser(Model model, HttpSession session, HttpServletRequest request, 
							RedirectAttributes rtts, HttpServletResponse response) {
		String urlPath = "";
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		model.addAttribute("request", request);
		model.addAttribute("response", response);
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String ip = request.getRemoteAddr();
		MemberDto idCheckResult = mDao.idCheck(id);
		if ( session.getAttribute("mDto") != null ){ // 세션 초기화  
            session.removeAttribute("mDto"); 
        }
		if( idCheckResult != null ) { // 해당 아이디 존재
			if(idCheckResult.getRole().equals("withdraw")) { // 탈퇴 계정 처리
				rtts.addFlashAttribute("failure", "withDrawId");
				urlPath = "redirect:login-form";
			} else {
				if( BCrypt.checkpw(pw, idCheckResult.getPw()) ) { // 암호화된 비밀번호와 평문 비교
					memberCommand = new MemberCookieSaveCommand(); // 쿠키 저장
					memberCommand.execute(sqlSession, model); 
					mDao.updateLog(ip, id); // 로그인 시 최근 접속 ip 업데이트
					session.setAttribute("mDto", idCheckResult);
					urlPath = "redirect:/";
				} else { // 아이디 비밀번호 검증 실패
					rtts.addFlashAttribute("failure", "missMatchIdPw");
					urlPath = "redirect:login-form";
				}
			}
		} else { // 해당 아이디가 없음
			rtts.addFlashAttribute("failure", "noneId");
			urlPath = "redirect:login-form";
		}
		return urlPath;
	}
	
	@RequestMapping("logout")
	public String logout(RedirectAttributes rtts, HttpSession session) {
		rtts.addFlashAttribute("isLogout", "YES");
		session.invalidate(); 
        return "redirect:/";
	}
	
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "id-check", produces = "application/json; charset=UTF-8", method = RequestMethod.POST)
	public String idCheck(@RequestParam("id") String id) {
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		MemberDto mDto = mDao.idCheck(id);
		JSONObject obj = new JSONObject();
		if( mDto != null ) {
			obj.put("isValid", "FALSE");
		} else {
			obj.put("isValid", "TRUE");
		}
		return obj.toJSONString();
	}
	
	@RequestMapping(value = "add-member", method = RequestMethod.POST)
	public String addMember(@Valid MemberDto memberDto, BindingResult bdr, RedirectAttributes rtts,
							Model model, HttpServletRequest request) {
		String urlPath = "";
		model.addAttribute("memberDto", memberDto);
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		// 유효성 검증
		if( bdr.hasErrors() ) {
			urlPath = "join/joinPage";
		} else {
			memberCommand = new MemberJoinCommand();
			memberCommand.execute(sqlSession, model);
			urlPath = "redirect:login-form";
		}
		return urlPath;
	}
	
	@RequestMapping("my-page")
	public String myPage(Model model, HttpServletRequest request) {
		model.addAttribute("request", request);
		memberCommand = new MemberActivityCommand();
		memberCommand.execute(sqlSession, model);
		return "user/myPage";
	}
	
	@RequestMapping("member-update-page")
	public String memberUpdatePage() {
		return "user/memberUpdatePage";
	}
	
	@RequestMapping("member-update-confirm")
	public String memberUpdateConfirm(RedirectAttributes rtts, HttpServletRequest request) {
		String urlPath = "";
		String id = request.getParameter("confirmId");
		String pw = request.getParameter("confirmPw");
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		MemberDto mDto = mDao.idCheck(id);
		if( BCrypt.checkpw(pw, mDto.getPw()) ) {
			urlPath = "redirect:member-info";
			rtts.addFlashAttribute("mDto", mDto);
		} else {
			urlPath = "redirect:member-update-page";
			rtts.addFlashAttribute("flag", "1");
		}
		return urlPath;
	}
	
	@RequestMapping("member-info")
	public String memberInfo(Model model) {
		model.addAttribute("memberDto", new MemberDto());
		return "user/memberInfo";
	}
	
	@RequestMapping(value = "member-update", method = RequestMethod.POST)
	public String memberUpdate(@Valid MemberDto memberDto, BindingResult bdr, HttpServletRequest request,
								RedirectAttributes rtts, Model model) {
		String urlPath="";
		model.addAttribute("memberDto", memberDto);
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		if( bdr.hasFieldErrors("email") || bdr.hasFieldErrors("nickname") || bdr.hasFieldErrors("name") ) {
			urlPath = "user/memberInfo";
		} else {
			memberCommand = new MemberUpdateCommand();
			memberCommand.execute(sqlSession, model);
			urlPath = "redirect:login-form";
		}
		return urlPath;
	}
	
	@RequestMapping("pw-update-page")
	public String pwUpdatePage() {
		return "user/pwUpdatePage";
	}
	
	@RequestMapping("pw-update")
	public String pwUpdate(Model model, RedirectAttributes rtts, HttpServletRequest request) {
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		String urlPath = "";
		String oldPw = request.getParameter("oldPw");
		String id = request.getParameter("id");
		MemberDto mDto = mDao.idCheck(id);
		if( mDto != null ) {
			if( BCrypt.checkpw(oldPw, mDto.getPw()) ) {
				memberCommand = new MemberPwUpdateCommand();
				memberCommand.execute(sqlSession, model);
				urlPath = "redirect:login-form";
			} else {
				rtts.addFlashAttribute("isPwUpdate", "NO");
				urlPath = "redirect:member-update-page";
			}
		}
		return urlPath;
	}
	
	@RequestMapping("member-drop-page")
	public String memberDropPage() {
		return "user/memberDropPage";
	}
	
	@RequestMapping("member-drop")
	public String memberDrop(Model model, HttpServletRequest request, RedirectAttributes rtts) {
		model.addAttribute("rtts", rtts);
		model.addAttribute("request", request);
		memberCommand = new MemberDropCommand();
		memberCommand.execute(sqlSession, model);
		return "redirect:/";
	}
	
	@RequestMapping("find-account-page")
	public String findAccountPage(Model model) {
		model.addAttribute("memberDto", new MemberDto());
		return "login/findAccountPage";
	}
	
	@RequestMapping(value="find-account", method=RequestMethod.POST)
	public String findAccount(@Valid MemberDto mDto, BindingResult bdr, Model model,
							  RedirectAttributes rtts) {
		MemberDao mDao = sqlSession.getMapper(MemberDao.class);
		MemberDto memberDto = mDao.emailConfirm(mDto);
		String urlPath = "";
		if( bdr.getFieldError("email") != null ) {
			urlPath = "login/findAccountPage";
		} else {
			if( memberDto != null ) {
				rtts.addFlashAttribute("mDto", memberDto);
				urlPath = "redirect:confirm-pass";
			} else {
				rtts.addFlashAttribute("isEmailConfirm", "NO");
				urlPath = "redirect:find-account-page";
			}
		}
		return urlPath;
	}
	
	@RequestMapping("new-password-page")
	public String newPasswordPage(Model model, HttpServletRequest request) {
		String uuid = request.getParameter("uuid"); 
		String id = request.getParameter("id");
		model.addAttribute("uuid", uuid);
		model.addAttribute("id", id);
		return "login/newPasswordPage";
	}
	
	@RequestMapping("new-password")
	public String newPassword(Model model, HttpServletRequest request, RedirectAttributes rtts) {
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		memberCommand = new MemberNewPasswordCommand();
		memberCommand.execute(sqlSession, model);
		return "redirect:login-form";
		
	}
	
	
}
