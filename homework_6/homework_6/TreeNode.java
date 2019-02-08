package homework_6;

import java.util.List;  
import java.util.ArrayList;
import java.io.File;
import java.io.Serializable;  
  
@SuppressWarnings("serial")
public class TreeNode implements Serializable {  
    private int parentId;  
    private int selfId;
    private File file;
    private String path;
	private String name;
    private long size;
    private long last_time;
    protected TreeNode parentNode;  
    protected List<TreeNode> childList;
    protected boolean isFile;

    
    
    public TreeNode(File file,int selfID,int parentID,String path, boolean isfile) {
    //  统计文件夹大小，记录last-time，记录name!!!!!!!!!!
    	this.isFile = isfile;
    	this.path = path;
    	this.parentId = parentID;
    	this.selfId = selfID;
    	this.file = file;
    	childList = new ArrayList<TreeNode>();
    	this.name = file.getName();
    	this.last_time = file.lastModified();
    	this.size = calSize(file);
    }  
 
//初始化根节点
    public TreeNode(File file,int selfID,String path, boolean isfile) {
        //  统计文件夹大小，记录last-time，记录name!!!!!!!!!!改进Tostring!!!
    		this.isFile = isfile;
        	this.path = path;
        	this.selfId = selfID;
        	this.file = file;
        	childList = new ArrayList<TreeNode>(); 
        	this.name = file.getName();
        	this.last_time = file.lastModified();
        	this.size = calSize(file);
        }  
    
    public long calSize(File file){
    	long size = 0;
    	File [] files;
    	if (file.isDirectory()){
    		files = file.listFiles();
    		for (File temp:files){
    			if (!temp.isDirectory()){
    				size += temp.length();
    			}
    		}
    	}
    	
    	else {
    		size = file.length();
    	}
    	return size;
    	
    }
    
    @Override
    public String toString(){
    	return String.valueOf(selfId)+" "+String.valueOf(parentId)+" "+path;
    }
  
//    public TreeNode(TreeNode parentNode) {  
//        this.getParentNode();  
//        initChildList();  
//    }  

//  判断两个节点是否相同  
    public boolean equals(TreeNode node){
    	if (node.equals(this)){
    		return true;
    	}
    	return false;
    }
  
   
  
    /* 插入一个child节点到当前节点中 */  
    public void addChildNode(TreeNode treeNode) {  
        initChildList();  
        childList.add(treeNode);  
    }  
  
    public void initChildList() {  
        if (childList == null)  
            childList = new ArrayList<TreeNode>();  
    }  
  
    public boolean isValidTree() {  
        return true;  
    }  
  
  
    /* 返回当前节点的晚辈集合 */  
    public List<TreeNode> getJuniors() {  
        List<TreeNode> juniorList = new ArrayList<TreeNode>();  
        List<TreeNode> childList = this.getChildList();  
        if (childList == null) {  
            return juniorList;  
        } else {  
            int childNumber = childList.size();  
            for (int i = 0; i < childNumber; i++) {  
                TreeNode junior = childList.get(i);  
                juniorList.add(junior);  
                juniorList.addAll(junior.getJuniors());  
            }  
            return juniorList;  
        }  
    }  
  
    /* 返回当前节点的孩子集合 */  
    public List<TreeNode> getChildList() {  
        return childList;  
    }  
  
    /* 找到一颗树中某个节点 */  
    public TreeNode findTreeNodeById(int id) {  
        if (this.selfId == id)  
            return this;  
        if (childList.isEmpty() || childList == null) {  
            return null;  
        } else {  
            int childNumber = childList.size();  
            for (int i = 0; i < childNumber; i++) {  
                TreeNode child = childList.get(i);  
                TreeNode resultNode = child.findTreeNodeById(id);  
                if (resultNode != null) {  
                    return resultNode;  
                }  
            }  
            return null;  
        }  
    }
    
    public TreeNode findTreeNodeByPath(String p) {  
        if (this.path.equals(p))  
            return this;  
        if (childList.isEmpty() || childList == null) {  
            return null;  
        } else {  
            int childNumber = childList.size();  
            for (int i = 0; i < childNumber; i++) {  
                TreeNode child = childList.get(i);  
                TreeNode resultNode = child.findTreeNodeByPath(p);  
                if (resultNode != null) {  
                    return resultNode;  
                }  
            }  
            return null;  
        }  
    }  
  
    /* 遍历一棵树，层次遍历 */  
    public void traverse() {  
//        if (selfId < 0)  
//            return;  
        print(this.selfId);  
        if (childList == null || childList.isEmpty())  {
        	System.out.println(this);
        	return;  
        }
        System.out.println(this);
        int childNumber = childList.size();  
        for (int i = 0; i < childNumber; i++) {  
            TreeNode child = childList.get(i);  
            child.traverse();  
        }  
    }  
  
    public void print(String content) {  
        System.out.println(content);  
    }  
  
    public void print(int content) {  
        System.out.println(String.valueOf(content));  
    }  
  
    public void setChildList(List<TreeNode> childList) {  
        this.childList = childList;  
    }  
  
    public int getParentId() {  
        return parentId;  
    }  
  
    public void setParentId(int parentId) {  
        this.parentId = parentId;  
    }  
  
    public int getSelfId() {  
        return selfId;  
    }  
  
    public void setSelfId(int selfId) {  
        this.selfId = selfId;  
    }  
  
    public TreeNode getParentNode() {  
        return parentNode;  
    }  
  
    public void setParentNode(TreeNode parentNode) {  
        this.parentNode = parentNode;  
    }   
    
    public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getLast_time() {
		return last_time;
	}

	public void setLast_time(long last_time) {
		this.last_time = last_time;
	}
}  
