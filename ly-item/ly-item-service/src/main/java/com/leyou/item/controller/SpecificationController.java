package com.leyou.item.controller;

import com.leyou.item.domain.SpecGroup;
import com.leyou.item.domain.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {
    @Autowired
    private SpecificationService service;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecGroupsByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(service.querySpecGroupsByCid(cid));
    }

    /**
     * 新增规格分组
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveSpecGroup(@RequestBody SpecGroup specGroup){
        service.saveSpecGroup(specGroup);
        return ResponseEntity.ok(null);
    }

    /**
     * 修改规格分组
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateSpecGroup(@RequestBody SpecGroup specGroup){
        service.updateSpecGroup(specGroup);
        return ResponseEntity.ok(null);
    }

    /**
     * 根据分组id删除规格分组
     * @param gid
     * @return
     */
    @DeleteMapping("/group/{gid}")
    public ResponseEntity<Void> deleteSpecGroup(@PathVariable("gid")Long gid){
        service.deleteSpecGroup(gid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 通过组id查询规格参数
     * @param gid
     * @param cid
     * @param searching
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParamList(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(service.querySpecParamList(gid,cid,searching));
    }

    /**
     * 新增规格参数
     * @param specParam
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveSpecParam(@RequestBody SpecParam specParam){
        service.saveSpecParam(specParam);
        return ResponseEntity.ok(null);
    }

    /**
     * 修改规格参数
     * @param specParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateSpecParam(@RequestBody SpecParam specParam){
        System.out.println(specParam);
        service.updateSpecParam(specParam);
        return ResponseEntity.ok(null);
    }

    /**
     * 删除规格参数
     * @param pid
     * @return
     */
    @DeleteMapping("/param/{pid}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("pid") Long pid){
        service.deleteSpecParam(pid);
        return ResponseEntity.ok(null);
    }

    /**
     * 根据cid查询分类组集合
     */
    @GetMapping("/group")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(service.queryListByCid(cid));
    }


    @GetMapping("{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> list = this.service.querySpecsByCid(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    /**
     * 根据分类id查询该分类下的规格参数组及组下的参数
     * @param cid
     * @return
     */
    @GetMapping("/groupsAndParam/{cid}")
    public ResponseEntity<List<SpecGroup>> findSpecGroupWithParamByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> groups = service.findSpecGroupWithParamByCid(cid);

        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(groups);
    }
}
