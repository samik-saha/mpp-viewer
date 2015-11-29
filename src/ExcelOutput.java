import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOutput extends SwingWorker<Void, Void> {
	Workbook wb;
	Sheet sheet;
	short rowIndex = 0;
	private ProjectFile projectFile;
	private File outputFile;
	MainWindow mainWindow;
	CreationHelper createHelper;
	int maxTaskNameLength=0;
	int errorCode=0;

	public ExcelOutput(MainWindow mw,ProjectFile projectFile, File outputFile) {
		this.mainWindow=mw;
		this.projectFile=projectFile;
		this.outputFile=outputFile;
	}

	public void listHierarchy(ProjectFile file) {
		for (Task task : file.getChildTasks()) {
			Row row = sheet.createRow( rowIndex++);
			if (task.getName()==null) continue;
			createCells(task,row,"  ",false);
			listHierarchy(task, "  ");
			
		}
	}

	private void listHierarchy(Task task, String indent) {
		for (Task child : task.getChildTasks()) {
			//System.out.println(indent + "Task: " + child.getName());
			Row row = sheet.createRow( rowIndex++);
			if (child.getName()==null) continue;
			
			boolean leafNode = child.getChildTasks().isEmpty();
			createCells(child,row,indent,leafNode);
			listHierarchy(child, indent + "  ");
		}
	}

	public void writeToFile(File file) {
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
		    fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorCode=1;
			
		} catch (IOException e) {
			e.printStackTrace();
			errorCode=1;
		}
	}

	private void createCells(Task task, Row row, String indent, boolean leafNode) {
		CellStyle cellStyle;
		
		Font font = wb.createFont();
		if(!leafNode)
			font.setBold(true);
		
		row.createCell((short) 0).setCellValue(task.getID());
		
		cellStyle = wb.createCellStyle();
		cellStyle.setFont(font);
		Cell cell=row.createCell((short) 1);
		String taskName=indent+task.getName();
		cell.setCellValue(taskName);
		cell.setCellStyle(cellStyle);
		maxTaskNameLength=taskName.length() > maxTaskNameLength?taskName.length():maxTaskNameLength;
		
		String duration = MainWindow.fmt(task.getDuration().getDuration());
		if (duration.equals("1"))
			duration = duration + " day";
		else
			duration = duration + " days";
		
		row.createCell((short) 2).setCellValue(duration);
		
		
		cellStyle = wb.createCellStyle();
	    cellStyle.setDataFormat(
	        createHelper.createDataFormat().getFormat("m/d/yy h:mm AM/PM"));
	    
	    
		cell = row.createCell((short) 3);
		cell.setCellValue(task.getStart());
		cell.setCellStyle(cellStyle);
		
		cell=row.createCell((short) 4);
		cell.setCellValue(task.getFinish());
		cell.setCellStyle(cellStyle);
		
		
		row.createCell((short) 5).setCellValue(
				MainWindow.fmt(task.getPercentageComplete().doubleValue())
						+ "%");
	
		
		/* Get list of predecessors */
		List<Relation> predecessors = task.getPredecessors();

		/* Convert predecessor list to string of ids */
		String predecessorsStr = "";
		if (predecessors != null && predecessors.isEmpty() == false) {
			for (Relation relation : predecessors) {
				predecessorsStr = predecessorsStr
						+ relation.getTargetTask().getID() + ",";
			}
			predecessorsStr = predecessorsStr.substring(0,
					predecessorsStr.length() - 1);
		}
		
		row.createCell((short) 6).setCellValue(predecessorsStr);
		

		List<ResourceAssignment> resourceAssignments = task
				.getResourceAssignments();
		int resourceCount = 0;

		/* Create Comma Seperated List of resource names */
		String resourceNames = "";
		if (resourceAssignments != null
				&& resourceAssignments.isEmpty() == false) {
			resourceCount = resourceAssignments.size();
			for (ResourceAssignment assignment : resourceAssignments) {
				Resource resource = assignment.getResource();
				resourceNames = resourceNames
						+ (resource == null ? "" : resource.getName());
			}
		}
		
		row.createCell((short) 7).setCellValue(resourceNames);

		
	}

	@Override
	protected Void doInBackground() throws Exception {
		wb = new XSSFWorkbook();
		sheet = wb.createSheet("Project Plan");
		createHelper = wb.getCreationHelper();
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Font font = wb.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		Row row=sheet.createRow(rowIndex++);
		Cell cell = row.createCell(0);
		cell.setCellValue("ID");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("Task Name");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("Duration");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("Start");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("Finish");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("% Complete");
		cell.setCellStyle(style);
		cell = row.createCell(6);
		cell.setCellValue("Predecessor");
		cell.setCellStyle(style);
		cell = row.createCell(7);
		cell.setCellValue("Resources");
		cell.setCellStyle(style);
		
		sheet.setColumnWidth(1, 50 * 256);
		
		setProgress(10);
		listHierarchy(projectFile);
		setProgress(60);
		
		sheet.setColumnWidth(1, maxTaskNameLength*250);
		sheet.setColumnWidth(3, 17*250);
		sheet.setColumnWidth(4, 17*250);
		
		writeToFile(outputFile);
		setProgress(100);
		return null;
	}

	@Override
	protected void done() {
		// TODO Auto-generated method stub
		super.done();
		mainWindow.progressBar.setVisible(false);
		if (errorCode==0)
			mainWindow.lblStatus.setText("Exported to "+outputFile.getAbsolutePath());
		else{
			mainWindow.lblStatus.setText("Done");
			JOptionPane.showMessageDialog(mainWindow,"An error occurred while writing to file "+outputFile.getAbsolutePath(),"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
}
