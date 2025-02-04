package com.example.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.Employee;
import com.example.form.UpdateEmployeeForm;
import com.example.service.EmployeeService;


import jakarta.servlet.http.HttpSession;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired 
	private HttpSession session;  //★管理者名表示時追加

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面を出力します.
	 * 
	 * @param model モデル
	 * @return 従業員一覧画面
	 */
	@GetMapping("/showList")
	public String showList(Model model) {
		List<Employee> employeeList = employeeService.showList();
		String administratorName = (String) session.getAttribute("administratorName");
		model.addAttribute("administratorName", administratorName);  //管理者名の表示
		model.addAttribute("employeeList", employeeList);
		return "employee/list";
	}


	//追加分　氏名検索
	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
	@PostMapping("/search")
   public String searchEmployee (@RequestParam("search_name") String searchName, Model model){
	 List<Employee> searchResult = employeeService.searchEmployeesByName(searchName);
	 logger.info("検索結果のサイズ: " + searchResult.size());  // ヒットした件数をログ出力
	 if (!searchResult.isEmpty()) {
		model.addAttribute("employeeList", searchResult);
	 }
	return "employee/list";
   }
   //  model.addAttribute("employeeList", searchResult);



	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@GetMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		String administratorName = (String) session.getAttribute("administratorName");
		model.addAttribute("administratorName", administratorName); //commit6
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@PostMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		String administratorName = (String) session.getAttribute("administratorName");
		model.addAttribute("administratorName", administratorName); //commit6
		return "redirect:/employee/showList";
	}
}
