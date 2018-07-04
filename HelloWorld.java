package com.thunisoft.znbapt.api.controller.rest;

import com.thunisoft.znbapt.cases.service.CaseService;
import com.thunisoft.znbapt.datamodel.api.*;
import com.thunisoft.znbapt.utils.RequestUtil;
import io.swagger.annotations.*;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(value = "案件接口", tags = "Case")
@RequestMapping("/api")
@RestController
public class CasesController {

    @Autowired
    private CaseService caseService;

    @RequestMapping(value = "/users/{userId}/cases", method = RequestMethod.GET)
    @ApiOperation(
            value = "查询案件信息",
            responseReference = "Query<Case>")
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(
                            name = "caseStatus",
                            value = "案件状态",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "waitConfirm", mediaType = "待确认"),
                                    @ExampleProperty(value = "processing", mediaType = "办理中"),
                                    @ExampleProperty(value = "closed", mediaType = "已结案")
                            })
                    ),
                    @ApiImplicitParam(
                            name = "keyword",
                            value = "查询关键字",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "韩梅梅诉李雷等离婚纠纷一案", mediaType = "案件名称"),
                                    @ExampleProperty(value = "(2015)湘高院民初字第(00212)号", mediaType = "案号"),
                                    @ExampleProperty(value = "邓志刚", mediaType = "承办人"),
                                    @ExampleProperty(value = "肖芳", mediaType = "参与人")
                            })
                    ),
                    @ApiImplicitParam(
                            name = "ownerType",
                            value = "案件与人员所属关系",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "myUndertake", mediaType = "我承办"),
                                    @ExampleProperty(value = "myInvolvement", mediaType = "我参与"),
                                    @ExampleProperty(value = "myCourt", mediaType = "本庭")
                            })
                    ),
                    @ApiImplicitParam(
                            name = "endStatus",
                            value = "结案状态",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "closed", mediaType = "已结案"),
                                    @ExampleProperty(value = "archived", mediaType = "已归档")
                            })
                    ),
                    @ApiImplicitParam(
                            name = "closeTime",
                            value = "查询结案时间（之内）",
                            required = false,
                            paramType = "String"),
                    @ApiImplicitParam(
                            name = "beyondCloseTime",
                            value = "查询结案时间（之外）",
                            required = false,
                            paramType = "String"),
                    @ApiImplicitParam(
                            name = "hasCourt",
                            value = "查询是否开庭",
                            required = false,
                            paramType = "Boolean"),
                    @ApiImplicitParam(
                            name = "hasTrial",
                            value = "查询是否超审限",
                            required = false,
                            paramType = "Boolean"),
                    @ApiImplicitParam(
                            name = "hasDocumented",
                            value = "查询文书是否已完成",
                            required = false,
                            paramType = "Boolean"),
                    @ApiImplicitParam(
                            name = "pageNo",
                            value = "分页-页数",
                            required = false,
                            paramType = "Integer"),
                    @ApiImplicitParam(
                            name = "pageSize",
                            value = "分页-每页个数",
                            required = false,
                            paramType = "Integer"),
                    @ApiImplicitParam(
                            name = "orderBy",
                            value = "排序字段",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "mixTrialTime", mediaType = "审限")
                            })
                    ),
                    @ApiImplicitParam(
                            name = "order",
                            value = "排序规则",
                            required = false,
                            paramType = "String",
                            examples = @Example({
                                    @ExampleProperty(value = "desc", mediaType = "倒序"),
                                    @ExampleProperty(value = "asc", mediaType = "正序")
                            })
                    )
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = HttpStatus.SC_NOT_FOUND,
                            message = "未查询到则返回404"
                    ),
                    @ApiResponse(
                            code = HttpStatus.SC_UNAUTHORIZED,
                            message = "返回登录地址",
                            response = Unauthorized.class
                    ),
                    @ApiResponse(
                            code = HttpStatus.SC_FORBIDDEN,
                            message = "返回注销地址",
                            response = Forbidden.class
                    )
            }
    )
    public Query<Case> getCases(@PathVariable String userId, HttpServletRequest request) {
        Map<String, String> params = RequestUtil.getParams(request);
        return caseService.getCase(userId, params);
    }

    @RequestMapping(value = "/cases/{caseId}/processes", method = RequestMethod.GET)
    @ApiOperation(
            value = "查询案件流程信息",
            response = Processes.class)
    @ApiImplicitParams(
            value = {
                    @ApiImplicitParam(
                            name = "ajlb",
                            value = "案件类别",
                            required = true,
                            paramType = "Integer")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = HttpStatus.SC_NOT_FOUND,
                            message = "未查询到则返回404"
                    ),
                    @ApiResponse(
                            code = HttpStatus.SC_UNAUTHORIZED,
                            message = "返回登录地址",
                            response = Unauthorized.class
                    ),
                    @ApiResponse(
                            code = HttpStatus.SC_FORBIDDEN,
                            message = "返回注销地址",
                            response = Forbidden.class
                    )
            }
    )
    public Processes getProcesses(@PathVariable String caseId, HttpServletRequest request) {
        Map<String, String> params = RequestUtil.getParams(request);
        return caseService.getProcesses(caseId, params);
    }

    @RequestMapping(value = "/cases/sign", method = RequestMethod.GET)
    @ApiOperation(
            value = "签收案件,有承办人则为分案",
            response = Status.class)
    public Status confirmSign(
            @RequestParam(value = "cAjbhs") String cAjbhs,
            @RequestParam(value = "cCbr", required = false) String cCbr,
            @RequestParam(value = "dQsrq") String dQsrq,
            @RequestParam(value = "ajlb") String ajlb
    ) {
        return caseService.confirmSign(cAjbhs, cCbr, dQsrq, ajlb);
    }

    @RequestMapping(value = "/cases/public", method = RequestMethod.GET)
    @ApiOperation(
            value = "公开案件",
            response = Status.class)
    public Status ajgkByAjlb(
            @RequestParam(value = "cAjbhs") String cAjbhs,
            @RequestParam(value = "ajlb") String ajlb
    ) {
        return caseService.ajgkByAjlb(cAjbhs, ajlb);
    }
}
