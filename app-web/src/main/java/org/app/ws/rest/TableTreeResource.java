package org.app.ws.rest;

import java.util.ArrayList;
import java.util.List;

import org.app.domain.grid.service.ExternalDbService;
import org.app.domain.grid.vo.TableMetaData;
import org.app.framework.paging.PagingAssembler;
import org.app.framework.web.tree.TreeNode;
import org.app.framework.web.tree.TreeResult;
import org.app.repo.jpa.dao.DbInfoDao;
import org.app.repo.jpa.model.DbInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/dbinfo/customdb", produces = { "application/json", "application/xml" })
public class TableTreeResource {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    DbInfoDao dbInfoDao;
    
    @Autowired
    PagingAssembler pagingAssembler;
    
    @Autowired
    ExternalDbService customDbManager;

    
    @RequestMapping(value = "/table-tree", method = RequestMethod.GET)
    @ResponseBody
    public TreeResult list(@RequestParam String node) {
        TreeResult treeResult = new TreeResult();
        if("root".equals(node)){
            List<DbInfo> results = dbInfoDao.findAll(DbInfo.class);
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (DbInfo dbInfo : results) {
                TreeNode treeNode = new TreeNode();
                treeNode.setExpanded(false);
                treeNode.setLeaf(false);
                treeNode.setLoaded(false);
                treeNode.setText(dbInfo.getName());
                treeNode.setValue(dbInfo.getDbInfoId().toString());
                treeNode.setId(dbInfo.getDbInfoId());
                children.add(treeNode);
            }
            treeResult.setChildren(children);
        }else{
            try {
                Integer dbInfoId = Integer.parseInt(node);
                List<TableMetaData> results = customDbManager.getTableResults(dbInfoId);
                if(results == null){
                    treeResult.setSuccess(false);
                }else{
                    List<TreeNode> children = new ArrayList<TreeNode>();
                    for (TableMetaData table : results) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setExpanded(false);
                        treeNode.setLeaf(true);
                        treeNode.setLoaded(false);
                        treeNode.setText(table.getTableName());
                        treeNode.setValue(table.getTableName());
                        treeNode.setId(null);
                        children.add(treeNode);
                    }
                    treeResult.setChildren(children);
                }
            } catch (NumberFormatException e) {
                treeResult.setSuccess(false);
            }
        }
        return treeResult;
    }
}
