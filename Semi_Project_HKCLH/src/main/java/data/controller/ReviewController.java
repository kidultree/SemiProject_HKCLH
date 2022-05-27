package data.controller;

<<<<<<< HEAD
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

=======
>>>>>>> branch 'main' of https://github.com/kidultree/SemiProject_HKCLH.git
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
=======
>>>>>>> branch 'main' of https://github.com/kidultree/SemiProject_HKCLH.git

import data.dto.ReviewDto;
import data.mapper.ReviewMapperInter;

@Controller
@RequestMapping("/review")
public class ReviewController {
	@Autowired
	private ReviewMapperInter reviewMapper;
	
	@GetMapping("/form")
	public String form() {
		return "/review/reviewform";
	}
	
	
		@GetMapping("/list")
		public ModelAndView list() 
		{			
			ModelAndView mview = new ModelAndView();
			
			int totalCount = reviewMapper.getTotalReviewCount();
			List<ReviewDto> list = reviewMapper.getReviewList();
			
			mview.addObject("totalCount", totalCount);
			mview.addObject("list",list);
			
			mview.setViewName("/review/reviewlist");
			return mview;
			
		}
		
		
		
		@PostMapping("/insert")
		public String insert
		(@ModelAttribute ReviewDto dto)
		{
			reviewMapper.insertReview(dto);
			return "redirect:list";
		}
	}

