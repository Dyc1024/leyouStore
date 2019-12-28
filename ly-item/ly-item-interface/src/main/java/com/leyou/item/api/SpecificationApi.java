package com.leyou.item.api;

import com.leyou.item.domain.SpecGroup;
import com.leyou.item.domain.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    /**
     * 通过组id查询规格参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("/spec/params")
    public List<SpecParam> querySpecParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    /**
     * 通过分类id查询分组
     */
    @GetMapping("/spec/group")
    public List<SpecGroup> queryListByCid(@RequestParam("cid") Long cid);


    @GetMapping("/spec/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroups(@PathVariable("cid") Long cid);

    @GetMapping("/spec/params")
    public List<SpecParam> querySpecParam(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic);

    // 查询规格参数组，及组内参数
    @GetMapping("/spec/{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);

    /**
     * 根据分类id查询该分类下的规格参数组及组下的参数
     * @param cid
     * @return
     */
    @GetMapping("/spec/groupsAndParam/{cid}")
    List<SpecGroup> findSpecGroupWithParamByCid(@PathVariable("cid")Long cid);
}
