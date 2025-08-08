import java.util.Collections;
import java.util.Date;
import java.util.List;

public  class  DataNode {
	private int taskID;
    private String task;
    private Date startDate;
    private Date finishDate;
    private String duration;
    private String percentageComplete;
    private String predecessors;
    private int resourceCount;
    private String resourceNames;
    private List<DataNode> children;

    public DataNode(
    		int taskID,
    		String task, 
    		Date startDate, 
    		Date finishDate, 
    		String duration, 
    		String percentageComplete,
    		String predecessors,
    		int resourceCount,
    		String resourceNames,
    		List<DataNode> children) {
    	this.taskID=taskID;
        this.task = task;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.duration = duration;
        this.percentageComplete=percentageComplete;
        this.predecessors=predecessors;
        this.resourceCount=resourceCount;
        this.resourceNames=resourceNames;
        this.children = children;

        if (this.children == null) {
            this.children = Collections.emptyList();
        }
    }
    
    public int getID(){
    	return taskID;
    }

    public  String getTask () {
        return  task;
    }

    public Date getStartDate() {
        return  startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public String getDuration() {
        return  duration;
    }
    
    public String getPercentageComplete(){
    	return percentageComplete;
    }
    
    public String getPredecessors(){
    	return predecessors;
    }
    
    public int getResources(){
    	return resourceCount;
    }
    
    public String getResourceNames(){
    	return resourceNames;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPercentageComplete(String percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public void setPredecessors(String predecessors) {
        this.predecessors = predecessors;
    }

    public void setResourceCount(int resourceCount) {
        this.resourceCount = resourceCount;
    }

    public void setResourceNames(String resourceNames) {
        this.resourceNames = resourceNames;
    }

    public void setChildren(List<DataNode> children) {
        this.children = children;
    }

    public  List <DataNode> getChildren () {
        return children;
    }
    
    public int getChildCount(){
    	return children.size();
    }

    /**
     * Text from the JTree node.
     */
    public  String toString () {
        return  task;
    }
}