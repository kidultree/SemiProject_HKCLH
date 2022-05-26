package data.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import Util.FileUtil;
import data.dto.NoticeDto;
import data.dto.QnADto;
import data.mapper.MemberMapperInter;
import data.mapper.QnAMapperInter;
import data.service.QnABoardService;

@Controller
@RequestMapping("/qna")
public class QnAController {

	@Autowired
	private QnABoardService qnaService;
	
	@Autowired
	private MemberMapperInter memberMapper;
	
	@GetMapping("/form")
	public ModelAndView form(@RequestParam Map<String, String>map) {
		ModelAndView mview = new ModelAndView();
	      //답글일 경우 읽어야 할 5개의 값 (새 글일 경우는 값이 안넘어옴 = 모두 null)
	      String currentPage=map.get("currentPage");
	      String num=map.get("num");
	      String reg=map.get("reg");
	      String restep=map.get("restep");
	      String relevel=map.get("relevel");
	      
	      mview.addObject("currentPage",currentPage==null?"1":currentPage);
	      mview.addObject("num",num==null?"0":num);
	      mview.addObject("reg",reg==null?"0":reg);
	      mview.addObject("restep",restep==null?"0":restep);
	      mview.addObject("relevel",relevel==null?"0":relevel);
	      
	    mview.setViewName("/qna/qnaform");
		return mview;
		
	}
	
	@GetMapping("/list")
	public ModelAndView list(
			@RequestParam(defaultValue = "1") int currentPage
			
			) {
		
		ModelAndView mview = new ModelAndView();
		//페이징 처리
		int totalCount; //총 갯수
		int perPage=5; //한 페이지당 보여질 글의 갯수
		int perBlock=5; //한 블럭당 보여질 글의 갯수 (◀이전 1,2,3,4,5 다음▶)
		int totalPage; //총 페이지수
		int startNum; //한 페이지에서 보여질 시작 글번호
		int startPage; //한 블럭에서 보여질 시작 페이지 번호
		int endPage; //한 블럭에서 보여질 끝 페이지 번호
		int no; //각 페이지당 보여질 시작번호
		
		//총 글의 갯수를 구한다
		totalCount = qnaService.getTotalCount();
		//총 페이지 수
		totalPage = totalCount/perPage+(totalCount%perPage==0?0:1);
		
		startPage = (currentPage-1)/perBlock*perBlock+1;
		endPage = startPage+perBlock-1;
		
		if(endPage>totalPage) {
			endPage=totalPage;
		}
		
		startNum = (currentPage-1)*perPage;
		no = totalCount-(currentPage-1)*perPage;
		
		//데이터 가져오기
		List<QnADto> list = qnaService.getQnAList(startNum, perPage);
		
		//각 데이터에 id를 이용해서 이름 넣어주기
				for(QnADto dto:list)
				{
					String id = dto.getMid();
					String name = memberMapper.getmName(id);
					dto.setMid(name);
				
				}
		
		
		//model에 저장
		mview.addObject("currentPage",currentPage);
		mview.addObject("totalCount",totalCount);
		mview.addObject("totalPage",totalPage);
		mview.addObject("startPage",startPage);
		mview.addObject("endPage",endPage);
		mview.addObject("no",no);
		mview.addObject("list",list);
		
		mview.setViewName("/qna/qnalist");
		return mview;
	}
	
	@PostMapping("/insert")
	   public String insert
	   (@ModelAttribute QnADto dto,	
		@RequestParam String currentPage,
		@RequestParam ArrayList<MultipartFile> upload,
		HttpSession session,
		HttpServletRequest request
			   )
	   {
		//사진을 저장할 경우
		String path = request.getServletContext().getRealPath("/save");
		
		//세션으로부터 로그인한 아이디 얻기
		String mid = (String)session.getAttribute("mid");
		dto.setMid(mid); //dto에 id 넣기
		
		//사진을 업로드 안했을 경우 photos 에 'no'라고 저장
		if(upload.get(0).getOriginalFilename().equals("")) {
			dto.setQimg("no");
		}else {
			FileUtil fileUtil = new FileUtil();
			String photos = "";
			for(MultipartFile f:upload)
			{
				String rename = fileUtil.changeFileName(f.getOriginalFilename());
				photos+=rename+",";
				
				File file = new File(path+"\\"+rename);
				try {
					f.transferTo(file); //save 폴더에 업로드됨
				} catch (IllegalStateException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//마지막 컴마 제거
			photos = photos.substring(0,photos.length()-1);
			System.out.println(photos);
			dto.setQimg(photos);
		}
	
		
			//db update
		qnaService.insertQnA(dto);
	      return "redirect:list?currentPage="+ currentPage;
	   }
	
	@GetMapping("/content")
	public ModelAndView content(
			@RequestParam int num,
			@RequestParam String currentPage
			)
	
	{
		ModelAndView mview = new ModelAndView();

		//num에 해당하는 dto
		QnADto dto = qnaService.getData(num);
		//이름 넣어주기
		String name = memberMapper.getmName(dto.getMid());
		dto.setMid(name); 
		
		mview.addObject("dto", dto);
		mview.addObject("currentPage", currentPage);
		
		mview.setViewName("/qna/qnacontent");
		return mview;
	}
}
