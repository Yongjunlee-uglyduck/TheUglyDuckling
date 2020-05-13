package com.uglyduck.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uglyduck.command.board.BoardCommand;
import com.uglyduck.command.board.BoardDeleteCommand;
import com.uglyduck.command.board.BoardListCommand;
import com.uglyduck.command.board.BoardUpdateCommand;
import com.uglyduck.command.board.BoardViewCommand;
import com.uglyduck.command.board.BoardWriteCommand;
import com.uglyduck.webapp.dao.BoardDao;
import com.uglyduck.webapp.dto.BoardDto;

@Controller
public class BoardController {
	// Field
	@Autowired
	SqlSession sqlSession;
	BoardCommand boardCommand;
	
	// Method
	@RequestMapping("write-page")
	public String boardWrite() {
		return "board/writePage";
	}
	
	@RequestMapping("board-list")
	public String getBoardList(Model model, HttpServletRequest request) {
		model.addAttribute("request", request);
		boardCommand = new BoardListCommand();
		boardCommand.execute(sqlSession, model);
		return "board/boardList";
	}
	
	@RequestMapping("board-write")
	public String boardWrite(Model model, HttpServletRequest request, RedirectAttributes rtts, 
							@ModelAttribute BoardDto bDto) {
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		model.addAttribute("bDto", bDto);
		boardCommand = new BoardWriteCommand();
		boardCommand.execute(sqlSession, model);
		
		return "redirect:board-list";
	}
	
	@RequestMapping("board-view")
	public String boardView(Model model, HttpServletRequest request) {
		model.addAttribute("request", request);
		boardCommand = new BoardViewCommand();
		boardCommand.execute(sqlSession, model);
		return "board/boardView";
	}
	
	@RequestMapping("update-page")
	public String updatePage(Model model, HttpServletRequest request, @RequestParam int board_no) {
		BoardDao bDao = sqlSession.getMapper(BoardDao.class);
		model.addAttribute("bDto", bDao.boardView(board_no));
		return "board/updatePage";
	}
	
	@RequestMapping("board-update")
	public String boardUpdate(Model model, HttpServletRequest request, RedirectAttributes rtts) {
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		boardCommand = new BoardUpdateCommand();
		boardCommand.execute(sqlSession, model);
		return "redirect:board-list";
	}
	
	@RequestMapping("board-drop")
	public String boardDrop(Model model, HttpServletRequest request, RedirectAttributes rtts) {
		model.addAttribute("request", request);
		model.addAttribute("rtts", rtts);
		boardCommand = new BoardDeleteCommand();
		boardCommand.execute(sqlSession, model);
		return "redirect:board-list";
	}
	
}
