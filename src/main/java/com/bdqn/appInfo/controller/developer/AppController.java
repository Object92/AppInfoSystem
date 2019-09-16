package com.bdqn.appInfo.controller.developer;

import com.alibaba.fastjson.JSON;
import com.bdqn.appInfo.exception.BusinessExcpetion;
import com.bdqn.appInfo.exception.CommonReturnType;
import com.bdqn.appInfo.pojo.Category;
import com.bdqn.appInfo.pojo.Dev_User;
import com.bdqn.appInfo.pojo.Dictionary;
import com.bdqn.appInfo.pojo.Info;
import com.bdqn.appInfo.service.AppInfoService;
import com.bdqn.appInfo.service.CategoryService;
import com.bdqn.appInfo.service.DictionaryService;
import com.bdqn.appInfo.utils.Constants;
import com.bdqn.appInfo.utils.PageSupport;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/dev/flatform/app")
public class AppController {

    private Logger logger = Logger.getLogger(AppController.class);

    @Autowired
    private AppInfoService appInfoService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private CategoryService categoryService;

    /**
     * @Description: 获取APP分页信息
     * @param: [model, session, querySoftwareName, _queryStatus, _queryCategoryLevel1, _queryCategoryLevel2, _queryCategoryLevel3, _queryFlatformId, pageIndex]
     * @return: java.lang.String
     * @Date: 2019/07/19 16:38
     */
    @RequestMapping(value = "/list")
    public String getAppInfoList(Model model, HttpSession session,
                                 @RequestParam(value = "querySoftwareName", required = false) String querySoftwareName,
                                 @RequestParam(value = "queryStatus", required = false) String _queryStatus,
                                 @RequestParam(value = "queryCategoryLevel1", required = false) String _queryCategoryLevel1,
                                 @RequestParam(value = "queryCategoryLevel2", required = false) String _queryCategoryLevel2,
                                 @RequestParam(value = "queryCategoryLevel3", required = false) String _queryCategoryLevel3,
                                 @RequestParam(value = "queryFlatformId", required = false) String _queryFlatformId,
                                 @RequestParam(value = "pageIndex", required = false) String pageIndex) throws BusinessExcpetion {
        logger.info("getAppInfoList -- > querySoftwareName: " + querySoftwareName);
        logger.info("getAppInfoList -- > queryStatus: " + _queryStatus);
        logger.info("getAppInfoList -- > queryCategoryLevel1: " + _queryCategoryLevel1);
        logger.info("getAppInfoList -- > queryCategoryLevel2: " + _queryCategoryLevel2);
        logger.info("getAppInfoList -- > queryCategoryLevel3: " + _queryCategoryLevel3);
        logger.info("getAppInfoList -- > queryFlatformId: " + _queryFlatformId);
        logger.info("getAppInfoList -- > pageIndex: " + pageIndex);
        Long devId=((Dev_User) session.getAttribute(Constants.DEVUSER_SESSION)).getId();
        List<Info> appInfoList = null;
        List<Dictionary> statusList = null;
        List<Dictionary> flatFormList = null;
        List<Category> categoryLevel1List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<Category> categoryLevel2List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        List<Category> categoryLevel3List = null;//列出一级分类列表，注：二级和三级分类列表通过异步ajax获取
        //页面容量
        int pageSize = Constants.PAGESIZE;
        //当前页码
        Integer currentPageNo = 1;
        if (pageIndex != null) {
            currentPageNo = Integer.valueOf(pageIndex);
        }
        Integer queryStatus = null;
        if (_queryStatus != null && !_queryStatus.equals("")) {
            queryStatus = Integer.parseInt(_queryStatus);
        }
        Integer queryCategoryLevel1 = null;
        if (_queryCategoryLevel1 != null && !_queryCategoryLevel1.equals("")) {
            queryCategoryLevel1 = Integer.parseInt(_queryCategoryLevel1);
        }
        Integer queryCategoryLevel2 = null;
        if (_queryCategoryLevel2 != null && !_queryCategoryLevel2.equals("")) {
            queryCategoryLevel2 = Integer.parseInt(_queryCategoryLevel2);
        }
        Integer queryCategoryLevel3 = null;
        if (_queryCategoryLevel3 != null && !_queryCategoryLevel3.equals("")) {
            queryCategoryLevel3 = Integer.parseInt(_queryCategoryLevel3);
        }
        Integer queryFlatformId = null;
        if (_queryFlatformId != null && !_queryFlatformId.equals("")) {
            queryFlatformId = Integer.parseInt(_queryFlatformId);
        }
        //总数量(表)
        int totalCount=0;
        totalCount = appInfoService.getAppInfoCount(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId);
        //总页数
        PageSupport pages = new PageSupport();
        pages.setCurrentPageNo(currentPageNo);
        pages.setPageSize(pageSize);
        pages.setTotalCount(totalCount);
        int totalPageCount = pages.getTotalPageCount();
        //控制首页和尾页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        appInfoList=appInfoService.getAppInfoList(querySoftwareName, queryStatus, queryCategoryLevel1, queryCategoryLevel2, queryCategoryLevel3, queryFlatformId, devId, currentPageNo, pageSize);
        statusList = this.getDataDictionaryList("APP_STATUS");
        flatFormList = this.getDataDictionaryList("APP_FLATFORM");
        categoryLevel1List = categoryService.getAppCategoryListByParentId(null);
        
        model.addAttribute("appInfoList", appInfoList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("flatFormList", flatFormList);
        model.addAttribute("categoryLevel1List", categoryLevel1List);
        model.addAttribute("pages", pages);
        model.addAttribute("queryStatus", queryStatus);
        model.addAttribute("querySoftwareName", querySoftwareName);
        model.addAttribute("queryCategoryLevel1", queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2", queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3", queryCategoryLevel3);
        model.addAttribute("queryFlatformId", queryFlatformId);
        //二级分类列表和三级分类列表---回显
        if (queryCategoryLevel2 != null) {
            categoryLevel2List = getCategoryList(queryCategoryLevel1.toString());
            model.addAttribute("categoryLevel2List", categoryLevel2List);
        }
        if (queryCategoryLevel3 != null) {
            categoryLevel3List = getCategoryList(queryCategoryLevel2.toString());
            model.addAttribute("categoryLevel3List", categoryLevel3List);
        }
        return "developer/appinfolist";
    }

    /**
     * @Description: 根据parentId查询出相应的分类级别列表
     * @param: [pid]
     * @return: java.util.List<com.bdqn.appInfo.pojo.Category>
     * @Date: 2019/07/20 16:44
     */
    private List<Category> getCategoryList(String toString) throws BusinessExcpetion {
        List<Category> categoryLevelList = null;
        categoryLevelList = categoryService.getAppCategoryListByParentId(toString == null ? null : Integer.parseInt(toString));
        return categoryLevelList;
    }

    /**
     * @Description: 获取类型信息
     * @param: [typeCode]
     * @return: java.util.List<com.bdqn.appInfo.pojo.Dictionary>
     * @Date: 2019/07/19 16:46
     */
    public List<Dictionary> getDataDictionaryList(String typeCode) throws BusinessExcpetion {
        List<Dictionary> dictionaryList = null;
        dictionaryList = dictionaryService.getDataDictionaryList(typeCode);
        if (dictionaryList == null) {
            return null;
        }
        return dictionaryList;
    }

    /**
     * 根据parentId查询出相应的分类级别列表
     *
     * @param pid
     * @return
     */
    @RequestMapping(value = "/categorylevellist.json", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getAppCategoryList(@RequestParam String pid)throws BusinessExcpetion{
        logger.info("getAppCategoryList pid ============ " + pid);
        if(pid.equals("")){
            pid=null;
        }
        //        return getCategoryList(pid);
        return CommonReturnType.create(JSON.toJSON(getDataDictionaryList(pid)));
    }

    /**
     * @Description: 跳转到APP添加页面
     * @param: [appInfo]
     * @return: java.lang.String
     * @Date: 2019/07/22 10:07
     */
    @RequestMapping(value = "/appinfoadd", method = RequestMethod.GET)
    public String add(@ModelAttribute("appInfo") Info appinfo){
        return "developer/appinfoadd";
    }

    /**
     * @Description: 根据typeCode查询出相应的数据字典列表
     * @param: [tcode]
     * @return: java.util.List<com.bdqn.appInfo.pojo.Dictionary>
     * @Date: 2019/07/22 9:50
     */
    @RequestMapping(value = "/datadictionarylist.json", method = RequestMethod.GET)
    @ResponseBody
    public CommonReturnType getDataDicList(@RequestParam String tcode)throws BusinessExcpetion{
        logger.debug("getDataDicList tcode ============ " + tcode);
        //        return this.getDataDictionaryList(tcode);
        return CommonReturnType.create(JSON.toJSON(this.getDataDictionaryList(tcode)));
    }

    /**
     * @Description: 判断APKName是否唯一
     * @param: [APKName]
     * @return: java.lang.Object
     * @Date: 2019/07/22 10:10
     */
    @RequestMapping(value = "/apkexist.json", method = RequestMethod.GET)
    @ResponseBody
    public Object apkNameIsExit(@RequestParam("APKName") String apkname)throws BusinessExcpetion{
        //根据id、APKName查找appInfo
        Info appInfo=appInfoService.getAppInfo(null,apkname);
        if(StringUtils.isNullOrEmpty(apkname)){
            return CommonReturnType.create(null,"empty");
        }else {
            if(appInfo==null){
                return CommonReturnType.create(null,"fail");
            }else {
                return CommonReturnType.create(JSON.toJSON(appInfo));
            }
        }
    }

    //    @ResponseBody

    public String addSave(Info app, HttpSession session, HttpServletRequest request,
                          @RequestParam(value = "a_logoPicPath",required = false) MultipartFile attach){
        String logoPicPath = null;
        String logoLocPath = null;
        if(!attach.isEmpty()) {
            String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
            logger.info("uploadFile path: " + path);
            String oldFileName = attach.getOriginalFilename();//原文将名
            String prefix = FilenameUtils.getExtension(oldFileName);//源文件后缀
            int filesize = 5000000;
            if (attach.getSize() > filesize) {//上传文件大小不得超过50k
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_4);
                //                throw new BusinessExcpetion(EmBusinessError.UPLOADERROR);
                return "developer/appinfoadd";
            } else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
                    || prefix.equalsIgnoreCase("jepg") || prefix.equalsIgnoreCase("pneg")) {//上传图片格式
                String fileName = app.getApkname() + ".jpg";//上传LOGO图片命名:apk名称.apk
                File targetFile = new File(path, fileName);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                try {
                    attach.transferTo(targetFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "developer/appinfoadd";
                }
                logoPicPath = request.getContextPath() + "/statics/uploadfiles/" + fileName;
                logoLocPath = path + File.separator + fileName;
            } else {
                request.setAttribute("fileUploadError", Constants.FILEUPLOAD_ERROR_3);
                return "developer/appinfoadd";
            }
        }
            app.setCreatedby(((Dev_User) session.getAttribute(Constants.DEVUSER_SESSION)).getId());
            app.setCreationdate(new Date());
            app.setLogopicpath(logoPicPath);
            app.setLogolocpath(logoLocPath);
            app.setDevid(((Dev_User) session.getAttribute(Constants.DEVUSER_SESSION)).getId());
            app.setStatus(1L);

            try {
                if(appInfoService.add(app)){
                    return "redirect:/dev/flatform/app/list";
                }
            } catch (BusinessExcpetion businessExcpetion) {
                businessExcpetion.printStackTrace();
            }
            return "developer/appinfoadd";
        }

}
