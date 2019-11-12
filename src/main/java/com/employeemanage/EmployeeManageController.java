package com.employeemanage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.employeemanage.model.CaPho;
import com.employeemanage.model.Card;
import com.employeemanage.model.CoLang;
import com.employeemanage.model.CoProd;
import com.employeemanage.model.Company;
import com.employeemanage.model.EmpLang;
import com.employeemanage.model.EmpProd;
import com.employeemanage.model.Employee;
import com.employeemanage.model.Language;
import com.employeemanage.model.Photo;
import com.employeemanage.model.Product;
import com.employeemanage.service.CaPhoService;
import com.employeemanage.service.CardService;
import com.employeemanage.service.CoLangService;
import com.employeemanage.service.CoProdService;
import com.employeemanage.service.CompanyService;
import com.employeemanage.service.EmpLangService;
import com.employeemanage.service.EmpProdService;
import com.employeemanage.service.EmployeeService;
import com.employeemanage.service.LanguageService;
import com.employeemanage.service.PhotoService;
import com.employeemanage.service.ProductService;


/**
 * EmployeeManageController
 * 						アプリケーションの挙動を決定するController
 *
 * @author Otomo
 *
 * @param	AParam		メソッドの引数
 * @param	VParam		メソッドの中で定義される変数
 *
 * Development period	2019/10/16	--	2019/10/30	サーバーサイド完成（ver1.0分）
 * 									2019/10/31	--	2019/11/01	Bootstrapの導入・フロントサイド実装完成
 */


@Controller
public class EmployeeManageController {

	@Autowired
	EmployeeService employeeService;
	@Autowired
	CompanyService companyService;
	@Autowired
	LanguageService languageService;
	@Autowired
	CardService cardService;
	@Autowired
	ProductService productService;
	@Autowired
	EmpLangService empLangService;
	@Autowired
	EmpProdService empProdService;
	@Autowired
	PhotoService photoService;
	@Autowired
	CaPhoService caPhoService;
	@Autowired
	CoLangService coLangService;
	@Autowired
	CoProdService coProdService;

	@PersistenceContext
	EntityManager entityManager;

//	Test用画像保存フォルダ
	private final String PHOTO_DIRECTORY
	= "C:/Users/Inter/Desktop/otomo/JavaWork/EmployeeManager/static/img/";

	@PostConstruct
	public void init() {
		languageService.setEntityManager(entityManager);
		empLangService.setEntityManager(entityManager);
		empProdService.setEntityManager(entityManager);
		photoService.setEntityManager(entityManager);
		caPhoService.setEntityManager(entityManager);
		coLangService.setEntityManager(entityManager);
		coProdService.setEntityManager(entityManager);
		}

	/**
	 * employeeDetailメソッド
	 * 		Employee_detail.htmlを開く事が多い上に、必要な処理が多いため、メソッドとして分離した。
	 * 		GETで渡した従業員IDを基にEmployeeテーブルを検索し、そのレコードのresident_idによって処理を分岐させている。
	 *
	 * @AParam id							従業員ID
	 * @AParam employee				呼び出すハンドラーでnewしたEmployeeインスタンス
	 * @AParam residentCheck		検索した従業員が派遣されているのか、本社勤務であるのかのフラグ
	 *
	 * @VParam residentId				検索したEmployeeレコードのresident_id。
	 * @VParam empLangList		emp_langテーブルに記録されている従業員が習得しているスキルの一覧。
	 * @VParam langStr					従業員が習得しているスキルを文字列に整形したもの。HTMLではこれを表示する。
	 * @VParam empProdList			emp_prodテーブルに記録されている従業員が関与した実績のリスト。
	 * @VParam prodList				従業員が関与した実績のリスト。
	 */
	public void employeeDetail(Integer id, Employee employee,
			boolean residentCheck, ModelAndView mav)  {

		employee = employeeService.findById(id).orElse(null);
		Integer residentId = employee.getResidentId();

		mav.addObject("detailData", employee);

		if (residentId == 0) {

			mav.addObject("companyData", "本社勤務");
		} else {

			residentCheck = true;
			Company companyData = companyService.findById(residentId).orElse(null);
			mav.addObject("residentCompany", companyData);
		}
		mav.addObject("residentCheck", residentCheck);

		List<EmpLang> empLangList = empLangService.findByEmployeeIdOrderByIdAsc(id);

		String langStr = "";

		for (EmpLang laId : empLangList) {
			String lang = languageService.findById(laId.getLangId())
						.orElse(null).getName();
			langStr = langStr + lang + ", ";
		}
		mav.addObject("languageList", langStr);

		List<EmpProd> empProdList = empProdService.findByEmployeeIdOrderByIdAsc(id);
		List<Product> prodList = new ArrayList<>();

		for (EmpProd proId : empProdList) {

			Product hasProduct = productService.findById(proId.getProdId())
						.orElse(null);
			prodList.add(hasProduct);
		}
		mav.addObject("productList", prodList);
	}

	/**
	 * cardDetailメソッド
	 * 		/Card_detailを開くことが頻繁にあり、同一の処理が複数回記述されるため、隔離。
	 * 		詳細ページを開く対象のCardクラスのエンティティを引数として渡す。
	 *
	 * @AParam card						名刺ID。
	 *
	 * @VParam photoCheck			名刺に名刺画像を紐付けしているかどうかのフラグ。
	 * @VParam caPho					当該名刺IDと紐付けられている画像IDを持つエンティティ。
	 * @VPamra photo					当該名刺と紐付けられている名刺画像。
	 * @VParam filePath				名刺画像が保存されているパス。
	 * @VParam companyId			当該名刺が所属する企業のID。
	 * @VParam company				当該名刺が所属する企業情報のエンティティ。
	 */
	public void cardDetail(Card card, ModelAndView mav) {

		mav.setViewName("Card_detail");

		boolean photoCheck = false;

		CaPho caPho = caPhoService.findByCardId(card.getId());
		if (caPho != null) {

			Photo photo = photoService.findById(caPho.getPhotoId());
			photoCheck = true;
			String filePath = "/img/" + photo.getFileName();
			mav.addObject("filePath", filePath);
		}

		mav.addObject("photoCheck", photoCheck);
		Integer companyId = card.getCompanyId();

		Company company = companyService.findById(companyId).orElse(null);
		mav.addObject("companyData", company);
	}

	/**
	 * companyDetailメソッド
	 * 			Company_detail.htmlを開く事が多い上に、必要な処理が多いため、メソッドとして分離した。
	 * 			引数としてcompany_idを受け取り、詳細ページに表示する情報を各テーブルから検索する。
	 *
	 * @AParam comId			企業ID。
	 *
	 * @VParam company		Companyテーブルから取り出したエンティティインスタンス。
	 * @VParam useSkill		当該企業でよく用いられるスキルIDのリスト。
	 * @VParam skillStr			スキル名を","区切りで整形した文字列。
	 * @VParam coProdList	当該企業が関わった実績IDのリスト。
	 * @VParam productList	実績エンティティのリスト。
	 */
	public void companyDetail(int comId, ModelAndView mav) {

		mav.setViewName("Company_detail");

		Company company = companyService.findById(comId).orElse(null);
		mav.addObject("detailData", company);

		List<CoLang> useSkill = coLangService.findByCompanyIdOrderByIdAsc(comId);
		String skillStr = "";

		for (CoLang skill : useSkill) {

			Language lang = languageService.findById(skill.getLangId()).orElse(null);
			skillStr = skillStr + lang.getName() + ",";
		}
		mav.addObject("skillStr", skillStr);

		List<CoProd> coProdList = coProdService.findByCompanyIdOrderByIdAsc(comId);
		List<Product> productList = new ArrayList<>();

		for (CoProd coProd : coProdList) {

			Product product = productService.findById(coProd.getProdId()).orElse(null);

			if (product != null) {

				productList.add(product);
			}
		}
		mav.addObject("productList", productList);
	}

	/**
	 * prpductDetailメソッド
	 * 		/Product_detailに頻繁にアクセスし、かつ処理も多くなってしまったので、まとめるべく隔離。
	 * 		引数に詳細ページを開こうとする実績のIDを渡す。
	 *
	 * @AParam prodId					実績ID。
	 *
	 * @VParam empProd				当該実績のIDと紐付けられている従業員のIDを持つエンティティ。
	 * @VParam empIdList				当該実績を持つ従業員のIDリスト。
	 * @VParam empList				当該実績を持つ従業員のエンティティリスト。
	 * @VParam coProd					当該実績のIDと紐付けられている企業のIDを持つエンティティ。
	 * @VParam companyList			当該実績と紐付けられている企業のエンティティリスト。
	 */
	public void productDetail(int prodId, ModelAndView mav) {

		mav.setViewName("Product_detail");

		List<EmpProd> empProd = empProdService.findByProductIdOrderByIdAsc(prodId);
		mav.addObject("empProdList", empProd);

		List<Integer> empIdList = new ArrayList<>();

		for (EmpProd obj : empProd) {
			empIdList.add(obj.getEmpId());
		}

		List<Employee> empList = new ArrayList<>();

		for (Integer empId : empIdList) {
			empList.add(employeeService.findById(empId).orElse(null));
		}
		mav.addObject("empList", empList);

		List<CoProd> coProd = coProdService.findByProductIdOrderByIdAsc(prodId);
		List<Company> companyList = new ArrayList<>();

		for (CoProd obj : coProd) {

			companyList.add(companyService.findById(obj.getComId()).orElse(null));
		}
		mav.addObject("companyList", companyList);
	}

	/**
	 * indexメソッド
	 * 		アプリケーションのトップページの出力。
	 *
	 * @return String			最終的に表示するページの情報をString形式で返す。
	 */
	@GetMapping("/")
	public String index() {

		return "index";
	}

	/**
	 * showEmployeeListメソッド
	 * 		Employeeテーブルの内容を一覧にして表示するページの出力。
	 * 		各従業員の名前が各従業員の詳細ページへのリンクになっている。
	 *
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam employeeDataList		Employeeテーブルから全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/Employee_List")
	public ModelAndView showEmployeeList(ModelAndView mav) {

		mav.setViewName("Employee_List");

		List<Employee> employeeDataList = employeeService.findAll();
		mav.addObject("dataList", employeeDataList);

		return mav;
	}

	/**
	 * formToAddEmployeeメソッド
	 * 		/Employee_Listから/Employee_AddにGETでアクセスした際に実行される。
	 * 		従業員の情報を新規に登録するページの出力。
	 *
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam	companyDataList		Companyテーブルから全件検索した結果のエンティティリスト。
	 * 								従業員が現在常駐している取引先企業をselect型式で選択する際に用いる。
	 */
	@GetMapping("/Employee_Add")
	public ModelAndView formToAddEmployee(@ModelAttribute Employee employee,
				ModelAndView mav) {

		mav.setViewName("Employee_Add");

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		return mav;
	}

	/**
	 * addEmployeeメソッド
	 * 		/Employee_AddからPOSTでアクセスした際に実行される。
	 * 		フォームに入力・選択された情報をEmployeeテーブルに1エンティティとして挿入する。
	 * 		挿入した後は、/Employee_Listにリダイレクトする。
	 *
	 * @AParam employee				/Employee_Addのフォームに入力した情報をエンティティとして取得した内容。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam employeeDataList		Employeeテーブルから全件検索した結果のエンティティリスト。
	 */
	@PostMapping("/Employee_Add")
	@Transactional(readOnly = false)
	public ModelAndView addEmployee(@Valid @ModelAttribute Employee employee,
			BindingResult result,
			ModelAndView mav) {

		if (result.hasErrors()) {

			mav.addObject("employee", employee);
			mav.setViewName("Employee_Add");
			List<Company> companyList = companyService.findAll();
			mav.addObject("dataList", companyList);
			return mav;
		}
		mav.setViewName("Employee_List");

		employeeService.save(employee);

		List<Employee> employeeDataList = employeeService.findAll();
		mav.addObject("dataList", employeeDataList);

		return mav;
	}

	/**
	 * detailOfEmployeeメソッド
	 * 		/Employee_Listから各従業員の詳細ページにアクセスした際に実行されるメソッド。
	 * 		GETパラメータで従業員idを渡し、Employeeテーブルを検索・表示する。
	 * 		検索したエンティティからresident_idを取得して、常駐している企業データも表示する。
	 * 		resident_idが'0'の場合、"本社勤務"と表示する。
	 *
	 * @AParam id				GETパラメータで渡された従業員ID
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam employee			GETパラメータのidでEmployeeテーブルを検索した結果のエンティティ。
	 * @VParam residentCheck	Employee_detail.htmlにおける三項演算子のチェッカー。
	 * 											（客先に常駐していればtrue, 本社勤務であればfalse）
	 */
	@GetMapping("/Employee_detail")
	public ModelAndView detailOfEmployee(@RequestParam("id") int id, ModelAndView mav) {

		mav.setViewName("Employee_detail");

		Employee employee = new Employee();
		boolean residentCheck = false;

		employeeDetail(id, employee, residentCheck, mav);

		return mav;
	}

	/**
	 * formToUpdateEmployeeメソッド
	 *		/Employee_detailから/Employee_UpdateにGETでアクセスした際に実行される。
	 *		従業員情報を編集するために情報を入力・選択するフォームページの出力。
	 *
	 * @AParam id							GETパラメータで渡された従業員ID。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam employee				GETで渡された従業員IDでEmployeeテーブルを検索した結果のエンティティ。
	 * 												当該エンティティを編集する。
	 * @VParam companyDataList	Companyテーブルから全件検索した結果のエンティティリスト。
	 * 												従業員が現在常駐している取引先企業をselect型式で選択する際に用いる。
	 * @VParam residentId				従業員が派遣されている企業のID。
	 * @VParam residentCheck		従業員が派遣されているかどうかの判定のためのフラグ。
	 */
	@GetMapping("/Employee_Update")
	public ModelAndView formToUpdateEmployee(@RequestParam("id") int id,
				ModelAndView mav) {

		mav.setViewName("Employee_Update");

		Employee employeeData = employeeService.findById(id).orElse(null);
		Integer residentId = employeeData.getResidentId();
		mav.addObject("detailData", employeeData);

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		boolean residentCheck = false;

		if (residentId != 0) { residentCheck = true; }

		mav.addObject("residentCheck", residentCheck);

		return mav;
	}

	/**
	 * updateEmployeeメソッド
	 * 		/Employee_AddからPOSTでアクセスした際に実行される。
	 * 		hidden属性で渡された従業員IDを基に、編集した情報を更新する。
	 * 		更新した後、編集した内容を確認するために/Employee_detailページにリダイレクトする。
	 * 		その際に、hidden属性で渡された従業員IDをGETパラメータで渡す。
	 *
	 * @AParam employee			/Employee_Updateで編集したエンティティ情報。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam id 					従業員Id。
	 * @VParam residentCheck	Employee_detail.htmlにおける三項演算子のチェッカー。
	 * 											（客先に常駐していればtrue, 本社勤務であればfalse）
	 */
	@PostMapping("/Employee_Update")
	@Transactional(readOnly = false)
	public ModelAndView updateEmployee(@Valid @ModelAttribute Employee employee,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {

			mav.setViewName("Employee_Update");
			Employee employeeData = employeeService.findById(employee.getId()).orElse(null);
			Integer residentId = employeeData.getResidentId();
			mav.addObject("detailData", employeeData);
			List<Company> companyDataList = companyService.findAll();
			mav.addObject("dataList", companyDataList);
			boolean residentCheck = false;
			if (residentId != 0) { residentCheck = true; }
			mav.addObject("residentCheck", residentCheck);
			return mav;
		}

		mav.setViewName("Employee_detail");

		employeeService.save(employee);

		int id = employee.getId();
		boolean residentCheck = false;

		employeeDetail(id, employee, residentCheck, mav);

		return mav;
	}

	/**
	 * formToSelectEmployeeSkillメソッド
	 * 		/Employee_detailからGETでアクセスした際に実行される。
	 *		従業員が習得している技術を編集するページを出力する。
	 *
	 * @AParam id								/Employee_detailからGETパラメータとして渡された従業員ID。
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam empLang					GETパラメータのIdをEmployee_idカラムの値として持つemp_langテーブルのエンティティリスト
	 * @VParam langId						empLangに入っているそれぞれのレコードが持つlanguage_idの値。
	 * @VParam languageDataList		Languageテーブルを全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/EmpLang")
	public ModelAndView formToSelectEmployeeSkill(@RequestParam("id") int id,
								ModelAndView mav) {

		mav.setViewName("emp_lang");
		mav.addObject("empId", id);

		List<EmpLang> empLang = empLangService.findByEmployeeIdOrderByIdAsc(id);
		List<Integer> langId = new ArrayList<>();

		for (EmpLang obj : empLang) {
			langId.add(obj.getLangId());
		}
		mav.addObject("langList", langId);

		List<Language> languageDataList = languageService.findAll();
		mav.addObject("dataList", languageDataList);

		return mav;
	}

	/**
	 * updateEmployeeSkillメソッド
	 *		/EmpLangからPOSTでアクセスした際に実行される。
	 *		選択したスキルをemp_langテーブルに記録していなければ、挿入する。
	 *		選択されなかったスキルの中で、以前記録されていたものがあれば、削除する。
	 *		SQL処理が完了すれば、/Employee_detailにリダイレクトする。
	 *		スキルが一つも選択されなければ、/Employee_detailにリダイレクトさせる。
	 *
	 * @AParam empId					/EmpLangからhidden属性で渡された従業員ID。
	 * @AParam langIdArray			選択されたスキルIDを配列で受け取った値。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam hasLang				従業員IDを基にemp_langテーブルを検索した結果のエンティティリスト。
	 * @VParam selectLangIdList	hasLangの1インスタンスが持つlanguage_idを持つリスト。
	 * @VParam empLang				既に習得技術として記録されているエンティティ
	 * @VParam addLang				新たに記録する習得技術のエンティティ。
	 * @VParam employee				employeeDetailメソッド用変数。
	 * @VParam residentCheck		employeeDetailメソッド用変数。
	 */
	@PostMapping("/EmpLang")
	@Transactional(readOnly = false)
	public ModelAndView updateEmployeeSkill(
				@RequestParam(value = "empId") Integer empId,
				@RequestParam(value = "langId", required = false) int[] langIdArray,
				ModelAndView mav) {

		if (langIdArray != null) {

			List<EmpLang> hasLang = empLangService.findByEmployeeIdOrderByIdAsc(empId);
			List<Integer> selectLangIdList = new ArrayList<>();

			for (int selectId : langIdArray) {
				selectLangIdList.add((Integer)selectId);
			}

			for (EmpLang hasLanguage : hasLang) {

				if (!selectLangIdList.contains(hasLanguage.getLangId())) {

					empLangService.delete(empId, hasLanguage.getLangId());
				}
			}

			for(int langId : selectLangIdList) {

				EmpLang empLang = empLangService
							.findByEmpIdAndLangId(empId, langId);

				if (empLang == null) {

					EmpLang addLang = new EmpLang();
					addLang.setEmpId(empId);
					addLang.setLangId(langId);
					empLangService.save(addLang);
				}
			}
		} else {

			empLangService.empDeleteAll(empId);
		}

		mav.setViewName("Employee_detail");

		Employee employee = new Employee();
		boolean residentCheck = false;

		employeeDetail(empId, employee, residentCheck, mav);

		return mav;
	}

	/**
	 * formToConnectProductWithEmployeeメソッド
	 * 		従業員が関与した実績を編集するページの出力。
	 * 		/Employee_detailからGETパラメータで従業員IDをもらう。
	 * @AParam id									GETパラメータで渡された従業員ID。
	 * @return@return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam empProd						従業員が関与した実績のリスト。
	 * @VParam prodId							実績IDを格納したリスト。
	 * @VParam productList					formの中で選択肢に使う全実績のリスト
	 * @VParam languageList					実績に紐付けするスキルのリスト。
	 */
	@GetMapping("/EmpProd")
	public ModelAndView formToConnectProductWithEmployee(@RequestParam("id") int id,
									ModelAndView mav) {

		mav.setViewName("emp_prod");
		mav.addObject("empId", id);

		List<EmpProd> empProd = empProdService.findByEmployeeIdOrderByIdAsc(id);
		List<Integer> prodId = new ArrayList<>();

		for (EmpProd obj : empProd) {

			prodId.add(obj.getProdId());
		}
		mav.addObject("prodList", prodId);

		List<Product> productList = productService.findAll();
		mav.addObject("dataList", productList);

		List<Language> languageList = languageService.findAll();
		mav.addObject("langList", languageList);

		return mav;
	}

	/**
	 * connectProductWithEmployeeメソッド
	 * 		/EmpProdからPOSTでアクセスした際に実行される。
	 * 		選択された実績一つについて、使用したスキルと関わった業務形態を文字列にしてemp_prodテーブルに挿入する。
	 * 		既に挿入されている実績が選択されていれば、記録されているレコードを更新する。
	 *
	 * @AParam empId							hidden属性で渡された従業員ID。
	 * @AParam prodId							選択された実績のID。
	 * @AParam langIdArray					選択されたスキルのIDを格納するリスト。
	 * @AParam type								text属性で記入された業務形態。
	 * @return@return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam addEmpProd					選択された結果の情報を持ったEmpProdエンティティのインスタンス。
	 * @VParam hasProd						選択された実績IDとGETパラメータの従業員IDを持つエンティティをemp_prodテーブルから検索した結果。
	 * @VParam skillStr							emp_prodのskillカラムに記録する使用したスキルを","区切りで整形した文字列。
	 */
	@PostMapping("/EmpProd")
	public ModelAndView connectProductWithEmployee(
				@RequestParam("empId") Integer empId,
				@RequestParam("prodId") int prodId,
				@RequestParam("skillId") int[] langIdArray,
				@RequestParam("type") String type,
				ModelAndView mav) {

		EmpProd addEmpProd = new EmpProd();
		addEmpProd.setEmpId(empId);
		addEmpProd.setProdId(prodId);

		EmpProd hasProd = empProdService.findByEmpIdAndProdId(empId, prodId);

		if (hasProd != null) {
			addEmpProd.setId(hasProd.getId());
		}

		String skillStr = "";
		for (int skill : langIdArray) {

			Language useSkill = languageService
								.findById(skill).orElse(null);
			skillStr = skillStr + useSkill.getName() + ",";
		}
		addEmpProd.setSkill(skillStr);
		addEmpProd.setType(type);

		empProdService.save(addEmpProd);

		mav.setViewName("Employee_detail");

		Employee employee = new Employee();
		boolean residentCheck = false;

		employeeDetail(empId, employee, residentCheck, mav);

		return mav;
	}

	/**
	 * showCompanyListメソッド
	 * 		Companyテーブルの内容を一覧にして表示するページの出力。
	 * 		各企業の名前がそれぞれの詳細ページへのリンクになっている。
	 *
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam companyDataList		Companyテーブルから全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/Company_List")
	public ModelAndView showCompanyList(ModelAndView mav) {

		mav.setViewName("Company_List");

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		return mav;
	}

	/**
	 * formToAddCompanyメソッド
	 * 		/Company_Listから/Company_AddにGETでアクセスした際に実行される。
	 * 		企業の情報を新規に登録するページの出力。
	 *
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 */
	@GetMapping("/Company_Add")
	public ModelAndView formToAddCompany(@ModelAttribute Company company,
					ModelAndView mav) {

		mav.setViewName("Company_Add");

		return mav;
	}

	/**
	 * addCompanyメソッド
	 * 		/Company_AddからPOSTでアクセスした際に実行される。
	 * 		フォームに入力された情報を1エンティティとしてCompanyテーブルに挿入する。
	 * 		挿入後は、/Company_Listにリダイレクトする。
	 *
	 * @AParam company					/Company_Addに入力された情報をエンティティとして取得した内容。
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam companyDataList		Companyテーブルから全件検索した結果のエンティティリスト。
	 */
	@PostMapping("/Company_Add")
	public ModelAndView addCompany(@Valid @ModelAttribute Company company,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {
			mav.setViewName("Company_Add");
			mav.addObject("company", company);
			return mav;
		}
		mav.setViewName("Company_List");

		companyService.save(company);

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		return mav;
	}

	/**
	 * detailOfCompanyメソッド
	 * 		/Company_Listから各企業の詳細ページにアクセスした際に実行されるメソッド。
	 * 		GETパラメータで企業idを渡し、Companyテーブルを検索・表示する。
	 *
	 * @AParam id						GETパラメータで渡された企業ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam company			CompanyテーブルをGETで渡された企業IDで検索した結果のエンティティ。
	 * @VParam useSkill			co_langテーブルに記録されている、主な使用スキルの一覧。
	 * @VParam skillStr				使用するスキルを","区切りで文字列に整形したもの。HTMLでこれを表示する。
	 */
	@GetMapping("/Company_detail")
	public ModelAndView detailOfCompany(@RequestParam("id") int id, ModelAndView mav) {

		companyDetail(id, mav);

		return mav;
	}

	/**
	 * formToUpdateCompanyメソッド
	 * 		/Company_detailからGETでアクセスした際に実行される。
	 * 		企業情報を入力するためのフォーム画面を出力する。
	 *
	 * @AParam id						/Company_detailからGETパラメータとして渡された企業ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam company			GETパラメータで渡された企業IDでCompanyテーブルを検索した結果のエンティティ。
	 */
	@GetMapping("/Company_Update")
	public ModelAndView formToUpdateCompany(@RequestParam("id") int id, ModelAndView mav) {

		mav.setViewName("Company_Update");

		Company company = companyService.findById(id).orElse(null);
		mav.addObject("detailData", company);

		return mav;
	}

	/**
	 * updateCompanyメソッド
	 * 		/Company_UpdateからPOSTでアクセスした際に実行される。
	 * 		hidden属性で渡されたIDを基にして、入力された情報を更新する。
	 * 		更新した後、編集した内容を確認するために/Company_detailページにリダイレクトする。
	 * 		その際に、hidden属性で渡された企業IDをGETパラメータで渡す。
	 *
	 * @AParam company				/Company_Updateに入力された情報を持つエンティティ。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam comId					当該企業のID。
	 */
	@PostMapping("/Company_Update")
	public ModelAndView updateCompany(@Valid @ModelAttribute Company company,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {
			mav.setViewName("Company_Update");
			mav.addObject("company", company);
			Company companyData = companyService.findById(company.getId()).orElse(null);
			mav.addObject("detailData", companyData);
			return mav;
		}

		companyService.save(company);

		Integer comId = company.getId();

		companyDetail(comId, mav);

		return mav;
	}

	/**
	 * formToSelectCompanySkillメソッド
	 * 		/Company_detailからGETでアクセスした際に実行される。
	 * 		当該企業で主に用いられるスキルを紐付けするためのフォームを出力する。
	 *
	 * @AParam id						/Company_detailからGETパラメータで渡された企業ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam coLang				すでに登録されているスキルのインスタンスリスト。
	 * @VParam hasLangList		すでに登録されているスキルIDのリスト。
	 * @VParam langList			Languageテーブルに記録されているスキルの一覧。
	 */
	@GetMapping("/CoLang")
	public ModelAndView formToSelectCompanySkill(
			@RequestParam("id") int id, ModelAndView mav) {

		mav.setViewName("co_lang");
		mav.addObject("comId", id);

		List<CoLang> coLang = coLangService.findByCompanyIdOrderByIdAsc(id);
		List<Integer> hasLangList = new ArrayList<>();

		for (CoLang lang : coLang) {

			hasLangList.add(lang.getLangId());
		}
		mav.addObject("langList", hasLangList);

		List<Language> langList = languageService.findAll();
		mav.addObject("dataList", langList);

		return mav;
	}

	/**
	 * selectCompanySkillメソッド
	 * 		/CoLangからPOSTでアクセスした際に実行される。
	 * 		入力フォームで選択されたスキルについて、①選択されていないスキルを既に登録されていれば当該レコードを削除し
	 * 		②選択されたスキルが未だ登録されていなければ新規で挿入する。
	 *
	 * @AParam comId				hidden属性で渡された従業員ID。
	 * @AParam langIdArray		一つ以上の選択されたスキルIDを配列に格納したもの。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam hasLang			渡された企業IDが持つco_langテーブルのエンティティリスト。
	 * @VParam selectLangList	選択されたスキルIDをリストにしたもの。
	 * @VParam coLang				選択されたスキルが既に登録されているかを調べたインスタンス。
	 * @VParam addLang			新規に登録する際に作成するco_langエンティティのインスタンス。
	 */
	@PostMapping("/CoLang")
	public ModelAndView selectCompanySkill(
			@RequestParam("comId") int comId,
			@RequestParam(value = "langId", required = false) int[] langIdArray,
			ModelAndView mav) {

		if (langIdArray != null) {

			List<CoLang> hasLang = coLangService.findByCompanyIdOrderByIdAsc(comId);
			List<Integer> selectLangList = new ArrayList<>();

			for (int selectId : langIdArray) {
				selectLangList.add((Integer)selectId);
			}

			for (CoLang hasLanguage : hasLang) {

				if (!selectLangList.contains(hasLanguage.getLangId())) {

					coLangService.delete(comId, hasLanguage.getLangId());
				}
			}

			for(int langId : selectLangList) {

				CoLang coLang = coLangService
							.findByComIdAndLangId(comId, langId);

				if (coLang == null) {

					CoLang addLang = new CoLang();
					addLang.setComId(comId);
					addLang.setLangId(langId);
					coLangService.save(addLang);
				}
			}
		} else {

			coLangService.coDeleteAll(comId);
		}

		companyDetail(comId, mav);

		return mav;
	}

	/**
	 * formToConnectCompanyWithProductメソッド
	 * 			/Company_detailからGETでアクセスした際に実行される。
	 * 			企業が関連する実績との紐付けを行うためのフォームページの出力。
	 *
	 * @AParam id						/Company_detailからGETパラメータで渡された企業ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam productList		選択肢として使用するための全実績リスト。
	 */
	@GetMapping("/CoProd")
	public ModelAndView formToConnectCompanyWithProduct(
				@RequestParam("id") int id, ModelAndView mav) {

		mav.setViewName("co_prod");
		mav.addObject("comId", id);

		List<Product> productList = productService.findAll();
		mav.addObject("dataList", productList);

		return mav;
	}

	/**
	 * connectCompanyWithProductメソッド
	 * 			/CoProdからPOSTでアクセスした際に実行される。
	 * 			selectで選んだ実績の情報と企業の情報を紐付けするためにco＿prodテーブルに挿入する。
	 *
	 * @AParam comId					/CoProdからhidden属性で渡された企業ID。
	 * @AParam prodId					/CoProdで選択した実績ID。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam addedCoProd		選択した実績が既に登録されているかを確かめるためのエンティティ。
	 */
	@PostMapping("/CoProd")
	public ModelAndView connectCompanyWithProduct(
				@RequestParam("comId") int comId,
				@RequestParam("prodId") int prodId,
				ModelAndView mav) {

		CoProd addedCoProd = coProdService.findByComIdAndProdId(comId, prodId);

		CoProd coProd = new CoProd();
		coProd.setComId(comId);
		coProd.setProdId(prodId);

		if (addedCoProd == null) {

			coProdService.save(coProd);
		}

		companyDetail(comId, mav);

		return mav;
	}

	/**
	 * showCardListメソッド
	 * 		Cardテーブルの内容を一覧にして表示するページの出力。
	 * 		名前が各名刺の詳細ページへのリンクになっている。
	 *
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam cardDataList			Cardテーブルから全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/Card_List")
	public ModelAndView showCardList(ModelAndView mav) {

		mav.setViewName("Card_List");

		List<Card> cardDataList = cardService.findAll();
		mav.addObject("dataList", cardDataList);

		return mav;
	}

	/**
	 * formToAddCardメソッド
	 * 		/Card_Listから/Card_AddへGETでアクセスした際に実行される。
	 * 		名刺を新規に登録するページの出力。
	 *
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam companyDataList		Companyテーブルから全件検索した結果のエンティティリスト。
	 * 													名刺の持ち主が在籍している企業をselect型式で選択する際に用いる。
	 */
	@GetMapping("/Card_Add")
	public ModelAndView formToAddCard(@ModelAttribute Card card,
				ModelAndView mav) {

		mav.setViewName("Card_Add");

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		return mav;
	}

	/**
	 * addCardメソッド
	 * 		/Card_AddからPOSTでアクセスした際に実行される。
	 * 		/Card_Addに入力・選択された情報を1エンティティとしてCardテーブルに挿入する。
	 * 		挿入後は/Card_Listにリダイレクトする。
	 *
	 * @AParam card						/Card_Addで入力された情報をエンティティとして取得した内容。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam cardDataList			Cardテーブルから全件検索した結果のエンティティリスト。
	 */
	@PostMapping("/Card_Add")
	public ModelAndView addCard(@Valid @ModelAttribute Card card,
				BindingResult result,
				ModelAndView mav) {

	if (result.hasErrors()) {
		mav.setViewName("Card_Add");
		mav.addObject("card", card);
		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);
		return mav;
	}

		mav.setViewName("Card_List");

		cardService.save(card);

		List<Card> cardDataList = cardService.findAll();
		mav.addObject("dataList", cardDataList);

		return mav;
	}

	/**
	 * detailOfCardメソッド
	 * 		/Card_ListからGETでアクセスした際に実行される。
	 * 		名刺の詳細情報を表示するページの出力。
	 *
	 * @AParam id						/Card_ListからGETパラメータで渡された名刺ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam card					詳細ページで表示するCardエンティティのインスタンス。
	 * @VParam photoCheck		表示されている名刺に名刺画像が紐付けられているかどうかのチェックフラグ。
	 * @VParam caPho				名刺IDを写真のIDを紐付けるca_phoエンティティのインスタンス。
	 * @VParam photo				紐付けられている名刺画像のID。
	 * @VParam filePath			HTMLページで画像を表示するためのファイルパス。
	 * 											画像は/src/main/resources/static/photo/の下にある。
	 */
	@GetMapping("/Card_detail")
	public ModelAndView detailOfCard(@RequestParam("id") int id, ModelAndView mav) {

		Card card = cardService.findById(id).orElse(null);
		mav.addObject("detailData", card);

		cardDetail(card, mav);

		return mav;
	}

	/**
	 * formToUpdateCardメソッド
	 * 		/Card_detailからGETでアクセスした際に実行される。
	 * 		登録された名刺の情報を編集するためのフォームの出力。
	 *
	 * @APparam id						/Card_detailからGETパラメータで渡された名刺ID。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam card						編集する元のデータのインスタンス。
	 * @VParam companyDataList	名刺と企業を紐付けするための企業一覧のリスト。
	 */
	@GetMapping("/Card_Update")
	public ModelAndView formToUpdateCard(@RequestParam("id") int id,
				ModelAndView mav) {

		mav.setViewName("Card_Update");

		Card card = cardService.findById(id).orElse(null);
		mav.addObject("detailData", card);

		List<Company> companyDataList = companyService.findAll();
		mav.addObject("dataList", companyDataList);

		return mav;
	}

	/**
	 * updateCardメソッド
	 * 		/Card_UpdateからPOSTでアクセスした際に実行される。
	 * 		選択・記入された内容でCardエンティティを更新するメソッド。
	 * 		更新したあとは/Card_detailにリダイレクトする。
	 *
	 * @AParam card						選択・入力された内容をもつCardエンティティのインスタンス。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam companyId			更新したインスタンスが持つ企業のID。
	 * @VParam company				/Card_detailで参照する当該名刺が所属する企業情報のインスタンス。
	 */
	@PostMapping("/Card_Update")
	public ModelAndView updateCard(@Valid @ModelAttribute Card card,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {
			mav.setViewName("Card_Update");
			Card cardData = cardService.findById(card.getId()).orElse(null);
			mav.addObject("detailData", cardData);
			List<Company> companyDataList = companyService.findAll();
			mav.addObject("dataList", companyDataList);
			return mav;
		}

		cardService.save(card);

		mav.addObject("detailData", card);

		cardDetail(card, mav);

		return mav;
	}

	/**
	 * formToAddPhotoメソッド
	 * 		/Card_detailからGETでアクセスした際に実行される。
	 * 		名刺に紐付けするための写真をアップロードするためのフォームの出力。
	 *
	 * @AParam id						/Card_detailからGETパラメータで渡された名刺ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam card					アップロードする写真と紐付ける名刺の情報を持ったインスタンス。
	 */
	@GetMapping("/Photo_Add")
	public ModelAndView formToAddPhoto(@RequestParam("id") int id,
					ModelAndView mav) {

		mav.setViewName("Photo_Add");

		Card card = cardService.findById(id).orElse(null);
		mav.addObject("cardData", card);

		return mav;
	}

	/**
	 * addPhotoメソッド
	 * 		/Photo_AddからPOSTでアクセスした際に実行される。
	 * 		①選択された画像をアップロード(/src/main/resources/static/photo/に保存)し、
	 * 		②アップロードされた画像の情報をPhotoテーブルに挿入し、
	 * 		③名刺と紐付けるためにca_phoテーブルにも情報を挿入する。
	 *
	 * @AParam file					POSTで送られたファイルが格納されているオブジェクト。
	 * @AParam id						/Photo_Addからhidden属性で送られた名刺ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam photo				アップロードした画像ファイルの名前をカラムとして持つエンティティ。
	 * @VParam fileName			アップロードした画像ファイルの名前。
	 * @VParam savePhoto		保存した画像ファイルの情報を持つインスタンス。
	 * @VParam uploadfile			画像をアップロードする場所を絶対パスで示したPath型のオブジェクト。
	 * @VParam bytes				OutputStream#writeメソッドでフォルダを保存するためにbyte型に変換したオブジェクト。
	 *
	 */
	@PostMapping("/Photo_Add")
	public ModelAndView addPhoto(
			@RequestParam("upload_file") MultipartFile file,
			@RequestParam("id") int id, ModelAndView mav) {

		boolean photoCheck = false;

		Card card = cardService.findById(id).orElse(null);

		if (!(file.getSize() > 0)) {

			mav.setViewName("Photo_Add");

			mav.addObject("cardData", card);
		} else {

			Photo photo = new Photo();
			String fileName = file.getOriginalFilename();
			photo.setFileName(fileName);

			photoService.save(photo);

			Photo savePhoto = photoService.findByLatestOne();
			CaPho caPho = new CaPho();

			caPho.setCardId(id);
			caPho.setPhotoId(savePhoto.getId());

			caPhoService.save(caPho);

			Path uploadfile = Paths.get(PHOTO_DIRECTORY + fileName);

			try (OutputStream os = Files.newOutputStream(uploadfile,
						StandardOpenOption.CREATE)) {

				byte[] bytes = file.getBytes();
				os.write(bytes);

				photoCheck = true;
			} catch (IOException e) {

				mav.addObject("error", "画像を保存できませんでした");
			}

			mav.addObject("detailData", card);

			cardDetail(card, mav);
		}

		mav.addObject("photoCheck", photoCheck);

		return mav;
	}

	/**
	 * showProductListメソッド
	 * 		Productテーブルの内容を一覧にして表示するページの出力。
	 * 		各実績名がそれぞれの詳細ページへのリンクになっている。
	 *
	 * @return ModelAndView				最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam productDataList		Productテーブルを全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/Product_List")
	public ModelAndView showProductList(ModelAndView mav) {

		mav.setViewName("Product_List");

		List<Product> productDataList = productService.findAll();
		mav.addObject("dataList", productDataList);

		return mav;
	}

	/**
	 * formToAddProductメソッド
	 * 		/Product_Listから/Product_AddへGETでアクセスした際に実行される。
	 * 		実績を新規に登録するページの出力。
	 *
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 */
	@GetMapping("/Product_Add")
	public ModelAndView formToAddProduct(@ModelAttribute Product product,
				ModelAndView mav) {

		return mav;
	}

	/**
	 * addProductメソッド
	 * 		/Product_AddからPOSTでアクセスした際に実行される。
	 * 		入力された情報を1エンティティとしてProductテーブルに挿入する。
	 * 		挿入後は/Product_Listへリダイレクトする。
	 *
	 * @AParam product					/Product_Addに入力された情報をエンティティとして取得した内容。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam productDataList	Productテーブルから全件検索した結果のエンティティリスト。
	 */
	@PostMapping("/Product_Add")
	public ModelAndView addProduct(@Valid @ModelAttribute Product product,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {
			mav.setViewName("Product_Add");
			mav.addObject("product", product);
			return mav;
		}

		mav.setViewName("Product_List");

		productService.save(product);

		List<Product> productDataList = productService.findAll();
		mav.addObject("dataList", productDataList);

		return mav;
	}

	/**
	 * detailOfProductメソッド
	 * 		/Product_ListからGETでアクセスした際に実行される。
	 * 		GETパラメータで渡された実績IDを持つ実績の詳細情報を表示するページの出力。
	 *
	 * @AParam id						GETパラメータで渡された実績ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam product				GETで渡された実績IDを持つProductのエンティティ。
	 */
	@GetMapping("/Product_detail")
	public ModelAndView detailOfProduct(@RequestParam("id") int id, ModelAndView mav) {

		Product product = productService.findById(id).orElse(null);
		mav.addObject("detailData", product);

		productDetail(id, mav);

		return mav;
	}

	/**
	 * formToUpdateProductメソッド
	 * 		/Product_detailからGETでアクセスした際に実行される。
	 * 		既に登録された実績の編集を行うためのフォームを出力する。
	 *
	 * @AParam id						GETパラメータで渡された実績ID。
	 * @return ModelAndView		最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam product				編集を行う実績エンティティ。
	 */
	@GetMapping("/Product_Update")
	public ModelAndView formToUpdateProduct(@RequestParam("id") int id, ModelAndView mav) {

		mav.setViewName("Product_Update");

		Product product = productService.findById(id).orElse(null);
		mav.addObject("detailData", product);
		mav.addObject("prodId", id);

		return mav;
	}

	/**
	 * updateProductメソッド
	 * 		/Product_UpdateからPOSTでアクセスした際に実行される。
	 * 		フォームに入力された情報を更新するメソッド。
	 *
	 * @AParam product					/Product_Updateのフォームで入力された情報を持つProductエンティティ。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 */
	@PostMapping("/Product_Update")
	public ModelAndView updataProduct(@Valid @ModelAttribute Product product,
				BindingResult result,
				ModelAndView mav) {

		if (result.hasErrors()) {
			mav.setViewName("Product_Update");
			Product productData = productService.findById(product.getId()).orElse(null);
			mav.addObject("detailData", productData);
			mav.addObject("prodId", product.getId());
			return mav;
		}

		productService.save(product);

		mav.addObject("detailData", product);

		productDetail(product.getId(), mav);

		return mav;
	}

	/**
	 * showSkillListメソッド
	 * 		/Skill_ListにGETでアクセスした際に実行される。
	 * 		従業員の習得スキル、各企業で主に用いられるスキル、各実績で用いられたスキルを一覧で表示するページ。
	 *
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam languageDataList	Languageテーブルを全件検索した結果のエンティティリスト。
	 */
	@GetMapping("/Skill_List")
	public ModelAndView showSkillList(@ModelAttribute Language language,
			ModelAndView mav) {

		mav.setViewName("Skill_List");

		List<Language> languageDataList = languageService.findAll();
		mav.addObject("dataList", languageDataList);

		return mav;
	}

	/**
	 * addSkillメソッド
	 * 		/Skill_ListにPOSTでアクセスした際に実行される。
	 * 		入力されたスキル名をLanguageテーブルに1エンティティとして挿入する。
	 * 		挿入後は/Skill_ListにGETでリダイレクトする。
	 *
	 * @param name						テキストフォームに入力されたスキル名をエンティティとして取得した内容。
	 * @return ModelAndView			最終的に表示するページの情報をModelAndView形式で返す。
	 *
	 * @VParam	languageDataList	Languageテーブルを全件検索した結果のエンティティリスト。
	 */
	@PostMapping("/Skill_List")
	public ModelAndView addSkill(@Valid @ModelAttribute Language language,
			BindingResult result,
			ModelAndView mav) {

		if (result.hasErrors()) {

			mav.setViewName("Skill_List");
			List<Language> languageDataList = languageService.findAll();
			mav.addObject("dataList", languageDataList);
			return mav;
		}

		mav.setViewName("Skill_List");

		languageService.save(language);

		List<Language> languageDataList = languageService.findAll();
		mav.addObject("dataList", languageDataList);
		return mav;
	}
}