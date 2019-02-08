package homework_6;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class Snapshot implements Runnable{
	private LinkedList<TreeNode> last_trees;
	private LinkedList<TreeNode> trees;
	private static int temp_id = 0;//每次初始化完一组树节点需要重新置0
	private TreeNode temp_root;
	protected boolean newsnap = false;
	protected int exec_count = 0;
	protected Summary summary;
	protected Detail detail;
	protected Recover recover;
	protected Rename rename;
	protected Modify modify;
	protected Path_change path_change;
	protected Size_change size_change;
	
	public Snapshot() {
		this.trees = new LinkedList<>();
		last_trees = null;
		this.summary = new Summary();
		this.detail = new Detail();
		this.recover = new Recover();
		this.rename = new Rename(this);
		this.modify = new Modify(this);
		this.path_change = new Path_change(this);
		this.size_change = new Size_change(this);
	}
	
	public LinkedList<TreeNode> getTrees(){
		return trees;
	}
	
	public LinkedList<TreeNode> getLastTrees(){
		return last_trees;
	}
	
//	首层开始的路径是路径数组中的父路径，注意判断父路径的null 首层parent id 为-1 区分监控区和工作区
	public LinkedList<TreeNode> scanDir(String path, int parent_id){
		LinkedList<TreeNode> node_list = new LinkedList<>();
		LinkedList<TreeNode> temp_list = new LinkedList<>();
		TreeNode temp_node;
		File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
//            if (files == null){
//            	temp_node = new TreeNode(file, temp_id, parent_id, file.getAbsolutePath());
//            	node_list.add(temp_node);
//            }
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
            } 
            else {
                for (File temp_file : files) {
                	
                    if (temp_file.isDirectory()) {
                        System.out.println("文件夹:" + temp_id + " " + temp_file.getAbsolutePath()+ " "+parent_id);
//                      存在递归文件夹的情况所以在此单独建立文件夹节点
                        temp_node = new TreeNode(temp_file, temp_id, parent_id, temp_file.getAbsolutePath(),false);
                        node_list.add(temp_node);
                        temp_id += 1;
                        temp_list  = scanDir(temp_file.getAbsolutePath(),temp_id-1);
                        node_list.addAll(temp_list);
                    } else {
                    	temp_node = new TreeNode(temp_file, temp_id, parent_id, temp_file.getAbsolutePath(),true);
                    	node_list.add(temp_node);
                        System.out.println("文件:" + temp_id + " " + temp_file.getAbsolutePath()+" "+ parent_id);
                        temp_id += 1;
                    }
                    
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
		return node_list;
	}
	
    private HashMap<String, TreeNode> nodeTOmap(LinkedList<TreeNode> nodes) {  
        int maxId = Integer.MAX_VALUE;  
        HashMap<String, TreeNode> nodeMap = new HashMap<String, TreeNode>();  
        Iterator<TreeNode> it = nodes.iterator();  
        while (it.hasNext()) {  
            TreeNode treeNode = (TreeNode) it.next();  
            int id = treeNode.getSelfId();  
            if (id < maxId) {  
                maxId = id;  
                this.temp_root = treeNode;  
            }  
            String keyId = String.valueOf(id); 
            nodeMap.put(keyId, treeNode);  
            // System.out.println("keyId: " +keyId);  
        }  
        return nodeMap;  
    }  
    
    private void putChildIntoParent(HashMap<String, TreeNode> nodeMap) {  
        Iterator<TreeNode> it = nodeMap.values().iterator();  
        while (it.hasNext()) {
            TreeNode treeNode = (TreeNode) it.next();
            if (treeNode.getSelfId() == -1) continue;
            int parentId = treeNode.getParentId();  
            String parentKeyId = String.valueOf(parentId);  
            if (nodeMap.containsKey(parentKeyId)) {  
                TreeNode parentNode = (TreeNode) nodeMap.get(parentKeyId);  
                if (parentNode == null) {  
                    System.out.println("FATAL ERROR: No such parent node");
                	return;  
                } else {  
                    parentNode.addChildNode(treeNode);
                    treeNode.setParentNode(parentNode);
                    // System.out.println("childId: " +treeNode.getSelfId()+" parentId: "+parentNode.getSelfId());  
                }  
            }
            else{
            	System.out.println("FATAL ERROR: No such parentKey");
            }
        }  
    }  
    
	public void takeShot(){
		LinkedList<TreeNode> treeNodes;
		HashMap<String, TreeNode> nodeMap;
		Iterator<String> it = Readin.routes.iterator();
		while (it.hasNext()){
			String work_dir = (String)it.next();
//			System.out.println(work_dir);
			File file = new File(work_dir);
			String par_dir = file.getParent();
			System.out.println(par_dir);
			if (par_dir.equals(null)) {
				par_dir = work_dir;
			}
			treeNodes = scanDir(par_dir, -1);
			nodeMap = nodeTOmap(treeNodes);
			if (!par_dir.equals(work_dir)){
				TreeNode root = new TreeNode(file, -1, par_dir, false);
				nodeMap.put("-1", root);
				temp_root = root;
			}
			putChildIntoParent(nodeMap);
			System.out.println("此次添加的根节点是 "+temp_root);
			trees.add(temp_root);
			temp_id = 0;
//			此处routes中的顺序会和工作区树节点列表顺序一样
		}
	}
	
	public void printShot(){
		Iterator<TreeNode> it = this.trees.iterator();
		while (it.hasNext()){
			TreeNode node = (TreeNode)it.next();
			node.traverse();
		}
	}
	
	public void refreshShot(){
		this.last_trees = this.trees;
		this.trees = null;
	}
	
	
	@Override
	public void run() {
//		take snapshot
		try {
			while (!Thread.interrupted()){
//				将两次快照分配给四个触发器线程
//				刷新快照
				synchronized (this){
					while (newsnap){
						wait();
					}
					refreshShot();
					takeShot();
					newsnap = true;
					exec_count = 0;
				}
				TimeUnit.SECONDS.sleep(3);
				summary.output();
				detail.output();
				this.notifyAll();
			}
		}catch (InterruptedException e){
			
		}
	}
	

}
