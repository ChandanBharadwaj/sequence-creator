package com.tmobile.annotations.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import com.tmobile.annotations.core.backend.TmoBackend;
import com.tmobile.annotations.core.component.TmoComponent;
import com.tmobile.annotations.core.component.TmoComponentFlow;
import com.tmobile.annotations.core.operation.TmoOperation;
import com.tmobile.annotations.core.operation.TmoOperationFlow;
import com.tmobile.annotations.processor.exception.SequenceProcessorException;
import com.tmobile.annotations.processor.util.ProcessorConst;
import com.tmobile.annotations.processor.utils.StringUtil;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.StringUtils;


@SupportedAnnotationTypes({
	"com.tmobile.annotations.core.backend.TmoBackend",
	"com.tmobile.annotations.core.component.TmoComponent",
	"com.tmobile.annotations.core.component.TmoComponentFlow",
	"com.tmobile.annotations.core.operation.TmoOperation",
	"com.tmobile.annotations.core.operation.TmoOperationFlow"
	})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TmoSequenceProcessor extends AbstractProcessor {
	public TmoSequenceProcessor() {
		super();
	}
	Map<String,TmoBackend> tmoDaoMap;
	List<String> tmoDaoHtmlList = new ArrayList<String>();
	List<String> tmoOperationHtmlList = new ArrayList<String>();
	Map<String,TmoComponent> tmoComponentMap;
	final String OPERATION_BACKGROUND_COLOR="#E7FFE7";
	final String MANAGER_BACKGROUND_COLOR="#FFF1F9";
	final String DAO_BACKGROUND_COLOR="#EFF6FF";
	final String BACKEND_BACKGROUND_COLOR="#fff9f4";
	final String NOTE_BG_COLOR="#FFFCF6";
	final String CONDITION_DIRECTION="right";
	int operations_count;
	String serviceName;
	String wsdl;
	
	Map<String,String> backendScriptMap;
	Map<String,String> daoScriptMap;
	Map<String,String> componentScriptMap;
	
	Map<String,String> daoCollection;
	Map<String,TmoComponentCollection> compCollection;
	Map<String,TmoComponentCollection> finalCompCollection;
	Set<String> tmoComponentClassList;
	Set<String> tmoDaoClassList;
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "chandan"+processingEnv.getOptions());
			tmoComponentMap = new HashMap<String,TmoComponent>();
			tmoComponentClassList = new LinkedHashSet<String>();
			tmoDaoClassList = new LinkedHashSet<String>();
			compCollection =new HashMap<String,TmoComponentCollection>();
			tmoDaoMap=new HashMap<String,TmoBackend>();
			for (Element elem : roundEnv.getElementsAnnotatedWith(TmoBackend.class)) {		
				String dao=TmoSequenceProcessor.getNameFromPackageName(elem.getEnclosingElement().toString());
				if(!tmoDaoMap.containsKey(dao+"#"+elem.getSimpleName())) {
					TmoBackend backend=elem.getAnnotation(TmoBackend.class);
					tmoDaoMap.put(dao+"#"+elem.getSimpleName(), elem.getAnnotation(TmoBackend.class));
					tmoDaoHtmlList.add("<li><div>"+backend.backend()+"-"+backend.backendOperation()+"</div></li>");
					tmoDaoClassList.add(dao);
				}
			}
			for (Element elem : roundEnv.getElementsAnnotatedWith(TmoComponent.class)) {
				String manager=TmoSequenceProcessor.getNameFromPackageName(elem.getEnclosingElement().toString());
				if(!tmoComponentMap.containsKey(manager+"#"+elem.getSimpleName())) {
					tmoComponentMap.put(manager+"#"+elem.getSimpleName(), elem.getAnnotation(TmoComponent.class));
					tmoComponentClassList.add(manager);
				}
			}
			for (Element elem : roundEnv.getElementsAnnotatedWith(TmoComponent.class)) {
				String manager=TmoSequenceProcessor.getNameFromPackageName(elem.getEnclosingElement().toString());
				compCollection.put(manager+"#"+elem.getSimpleName(), getCompCollection(manager+"#"+elem.getSimpleName(),elem.getAnnotation(TmoComponent.class)));
			}
			
			for (Element elem : roundEnv.getElementsAnnotatedWith(TmoOperation.class)) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,String.valueOf(++this.operations_count));
				long startTime=System.currentTimeMillis();
				TmoOperation tmoOp = elem.getAnnotation(TmoOperation.class);
				serviceName=TmoSequenceProcessor.getNameFromPackageName(elem.getEnclosingElement().toString());
				String operationName =elem.getSimpleName().toString();
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, serviceName+" - "+operationName);
				StringBuffer sb=new StringBuffer();
				sb.append("@startuml\n");
				sb=setupTitle(sb,serviceName,operationName,null);
				sb.append("participant SoapClient\n");
				sb.append("box \"Operation\" "+this.OPERATION_BACKGROUND_COLOR+ " \n");
				sb.append("participant "+operationName+"\n");
				sb.append("end box \n");
				TmoOperationFlow[] tmoFlowList=tmoOp.operationFlows();
				sb.append(createParticipants(tmoFlowList));
				sb.append("SoapClient-> "+operationName+" :  "+operationName+"Request \n");
				sb.append("activate "+operationName+"\n");
				for(TmoOperationFlow tmoOperationFlow:tmoFlowList) {
					if(StringUtil.isNotBlank(tmoOperationFlow.component()) && StringUtil.isNotBlank(tmoOperationFlow.componentOperation())) {
						// start component 
						if(this.tmoComponentMap.containsKey(tmoOperationFlow.component()+"#"+tmoOperationFlow.componentOperation())) {					
							TmoComponent tmoComponent=this.tmoComponentMap.get(tmoOperationFlow.component()+"#"+tmoOperationFlow.componentOperation());
							if(StringUtil.isNotBlank(tmoOperationFlow.component()) && StringUtil.isNotBlank(tmoOperationFlow.componentOperation().trim())) {						
								sb.append(operationName+" -> "+tmoOperationFlow.component()+" : "+(StringUtil.isNotBlank(tmoOperationFlow.componentInput())?tmoOperationFlow.componentInput() :tmoOperationFlow.componentOperation())+"\n");
								sb.append("activate "+tmoOperationFlow.component()+"\n");
								if(tmoOperationFlow.componentCondition()!=null && !tmoOperationFlow.componentCondition().isEmpty() && !tmoOperationFlow.componentCondition().trim().isEmpty()) {						
									sb.append("hnote "+this.CONDITION_DIRECTION+" "+operationName+" "+this.NOTE_BG_COLOR+": "+tmoOperationFlow.componentCondition()+"\n");
								}
							}
							if(tmoComponent.componentFlows().length>0){
								for(TmoComponentFlow tmoComponentFlow:tmoComponent.componentFlows()) {
									if(this.tmoComponentMap.containsKey(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation())){
										sb.append(tmoOperationFlow.component()+"-> "+tmoComponentFlow.component()+" :  "+tmoComponentFlow.componentOperation()+"\n");
										sb.append("activate "+tmoComponentFlow.component()+"\n");
										if(tmoComponentFlow.componentCondition()!=null && !tmoComponentFlow.componentCondition().isEmpty() && !tmoComponentFlow.componentCondition().trim().isEmpty()) {						
											sb.append("hnote "+this.CONDITION_DIRECTION+" "+tmoOperationFlow.component()+" "+this.NOTE_BG_COLOR+": "+tmoComponentFlow.componentCondition()+"\n");
										}
										String inner =getComponetScript(tmoComponentFlow).toString();
										System.out.println("getComponetScript"+ inner);
										this.sbInnerComp = new StringBuffer();
										sb.append(inner);
										sb.append(tmoComponentFlow.component()+" --> "+tmoOperationFlow.component()+": "+((StringUtils.isNotEmpty(tmoComponentFlow.componentOutput()))?tmoComponentFlow.componentOutput():tmoComponentFlow.componentOperation())+"\n");
										sb.append("deactivate "+tmoComponentFlow.component()+"\n");
									}else if(this.tmoDaoMap.containsKey(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation())) {
										TmoBackend backendInfo=this.tmoDaoMap.get(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation());
										sb.append(tmoOperationFlow.component()+"-> "+tmoComponentFlow.component()+" :  "+tmoComponentFlow.componentOperation()+"\n");
										sb.append("activate "+tmoComponentFlow.component()+"\n");
										if(tmoComponentFlow.componentCondition()!=null && !tmoComponentFlow.componentCondition().isEmpty() && !tmoComponentFlow.componentCondition().trim().isEmpty()) {						
											sb.append("hnote "+this.CONDITION_DIRECTION+" "+tmoOperationFlow.component()+" "+this.NOTE_BG_COLOR+": "+tmoComponentFlow.componentCondition()+"\n");
										}
										sb.append(tmoComponentFlow.component()+"-> "+backendInfo.backend()+" :  "+backendInfo.backendOperation()+"\n");
										sb.append("activate "+backendInfo.backend()+"\n");
										sb.append(backendInfo.backend()+" --> "+tmoComponentFlow.component()+": "+backendInfo.backendOperation()+"\n");
										sb.append("deactivate "+backendInfo.backend()+"\n");
										sb.append(tmoComponentFlow.component()+" --> "+tmoOperationFlow.component()+": "+((StringUtils.isNotEmpty(tmoComponentFlow.componentOutput()))?tmoComponentFlow.componentOutput():tmoComponentFlow.componentOperation())+"\n");
										sb.append("deactivate "+tmoComponentFlow.component()+"\n");
									}else {
										throw new SequenceProcessorException("Sequence creation failed for "+operationName+" operation.\n"
												+ tmoComponentFlow.component()+"."+tmoComponentFlow.componentOperation()+" not found !! \n"
														+ "Please check the @TmoComponentFlow declaration.");
									}
								}	
								//sb.append(tmoOperationFlow.component()+" --> "+operationName+" : "+((tmoOperationFlow.componentOutput()!=null && tmoOperationFlow.componentOutput().trim()!=null)?tmoOperationFlow.componentOutput():tmoOperationFlow.componentOperation())+"\n");
								//sb.append("deactivate "+tmoOperationFlow.component()+"\n");
								if(tmoOperationFlow.isEndOfFlow()) {
									sb.append("note left of "+ operationName +" #E1F5FE \n" + tmoOperationFlow.endOfFlowCondition()+"\n" + "end note\n");
									sb.append(operationName+" --> SoapClient: "+operationName+"Response\n");			
								}
							}
						}else if(this.tmoDaoMap.containsKey(tmoOperationFlow.component()+"#"+tmoOperationFlow.componentOperation())) {
							if(StringUtil.isNotBlank(tmoOperationFlow.component()) && StringUtil.isNotBlank(tmoOperationFlow.componentOperation().trim())) {						
								sb.append(operationName+" -> "+tmoOperationFlow.component()+" : "+(StringUtil.isNotBlank(tmoOperationFlow.componentInput())?tmoOperationFlow.componentInput() :tmoOperationFlow.componentOperation())+"\n");
								sb.append("activate "+tmoOperationFlow.component()+"\n");
								if(tmoOperationFlow.componentCondition()!=null && !tmoOperationFlow.componentCondition().isEmpty() && !tmoOperationFlow.componentCondition().trim().isEmpty()) {						
									sb.append("hnote "+this.CONDITION_DIRECTION+" "+operationName+" "+this.NOTE_BG_COLOR+": "+tmoOperationFlow.componentCondition()+"\n");
								}
							}
							TmoBackend backendInfo=this.tmoDaoMap.get(tmoOperationFlow.component()+"#"+tmoOperationFlow.componentOperation());
							/*sb.append(tmoOperationFlow.component()+"-> "+tmoOperationFlow.component()+" :  "+tmoOperationFlow.componentOperation()+"\n");
							sb.append("activate "+tmoOperationFlow.component()+"\n");
							if(tmoOperationFlow.componentCondition()!=null && !tmoOperationFlow.componentCondition().isEmpty() && !tmoOperationFlow.componentCondition().trim().isEmpty()) {						
								sb.append("hnote "+this.CONDITION_DIRECTION+" "+tmoOperationFlow.component()+" "+this.NOTE_BG_COLOR+": "+tmoOperationFlow.componentCondition()+"\n");
							}*/
							sb.append(tmoOperationFlow.component()+"-> "+backendInfo.backend()+" :  "+backendInfo.backendOperation()+"\n");
							sb.append("activate "+backendInfo.backend()+"\n");
							sb.append(backendInfo.backend()+" --> "+tmoOperationFlow.component()+": "+backendInfo.backendOperation()+"\n");
							sb.append("deactivate "+backendInfo.backend()+"\n");
						} else{
							throw new SequenceProcessorException("Sequence creation failed for "+operationName+" operation.\n"
									+tmoOperationFlow.component()+"."+tmoOperationFlow.componentOperation()+" not found !! \n"
											+ "Please check the @TmoOperationFlow declaration.");					
						}
						sb.append(tmoOperationFlow.component()+" --> "+operationName+" : "+(StringUtil.isNotBlank((tmoOperationFlow.componentOutput()))?tmoOperationFlow.componentOutput():tmoOperationFlow.componentOperation())+"\n");
						sb.append("deactivate "+tmoOperationFlow.component()+"\n");
						if(tmoOperationFlow.isEndOfFlow()) {
							sb.append("note left of "+ operationName +" #E1F5FE \n" + tmoOperationFlow.endOfFlowCondition()+"\n" + "end note\n");
							sb.append(operationName+" --> SoapClient: "+operationName+"Response\n");			
						}
					}
					
				}							
					sb.append(operationName+" --> SoapClient: "+operationName+"Response\n");
					sb.append("deactivate "+operationName+"\n");
					sb.append("@enduml");
					createDiagram(sb,operationName);
					tmoOperationHtmlList.add(createOperationHtml(operationName,tmoOp.operationDescription()));
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, sb.toString());
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "--- Process Completed : "+String.valueOf(System.currentTimeMillis()-startTime)+" Time(ms) ---");
			}
		}catch(Exception e) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, e.getMessage());
		}
		if(this.operations_count==roundEnv.getElementsAnnotatedWith(TmoOperation.class).size()) {			
			createHtmlFile(roundEnv,tmoDaoHtmlList,tmoOperationHtmlList);
			this.operations_count=0;
		}
	return true;
	}
	
	
	StringBuffer sbInnerComp=new StringBuffer();
	private String getComponetScript(TmoComponentFlow tmoComponentFlow) {
		StringBuffer sbComp=new StringBuffer();
		TmoComponent tmoComponent=this.tmoComponentMap.get(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation());
		for(TmoComponentFlow tmoInnerComponentFlow :tmoComponent.componentFlows()){
			if(StringUtil.isNotBlank(tmoInnerComponentFlow.component()) && StringUtil.isNotBlank(tmoInnerComponentFlow.componentOperation())) {						
				if(this.tmoComponentMap.containsKey(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation())){
					sbComp.append(tmoComponentFlow.component()+"-> "+tmoInnerComponentFlow.component()+" : "+(StringUtil.isNotBlank(tmoInnerComponentFlow.componentInput())?tmoInnerComponentFlow.componentInput() :tmoInnerComponentFlow.componentOperation())+"\n");
					sbComp.append("activate "+tmoInnerComponentFlow.component()+"\n");
					if(tmoInnerComponentFlow.componentCondition()!=null && !tmoInnerComponentFlow.componentCondition().isEmpty() && !tmoInnerComponentFlow.componentCondition().trim().isEmpty()) {						
						sbComp.append("hnote "+this.CONDITION_DIRECTION+" "+tmoComponentFlow.component()+" "+this.NOTE_BG_COLOR+": "+tmoInnerComponentFlow.componentCondition()+"\n");
					}	
					sbComp.append(getComponetScript(tmoInnerComponentFlow));
					sbComp.append(tmoInnerComponentFlow.component()+" --> "+tmoComponentFlow.component()+": "+((StringUtils.isNotEmpty(tmoInnerComponentFlow.componentOutput()))?tmoInnerComponentFlow.componentOutput():tmoInnerComponentFlow.componentOperation())+"\n");
					sbComp.append("deactivate "+tmoInnerComponentFlow.component()+"\n");
				}else if(this.tmoDaoMap.containsKey(tmoComponentFlow.component()+"#"+tmoComponentFlow.componentOperation())){
					sbComp.append(getDaoScript(tmoInnerComponentFlow));		
				}
			}
		}
		System.out.println("sbInnerComp : "+sbComp);
		sbInnerComp.append(sbComp);
		return sbInnerComp.toString();
	}

	private String getDaoScript(TmoComponentFlow tmoInnerDaoFlow) {
		StringBuffer sbInnerDao=new StringBuffer();
		TmoBackend backendInfo=this.tmoDaoMap.get(tmoInnerDaoFlow.component()+"#"+tmoInnerDaoFlow.componentOperation());
		sbInnerDao.append(tmoInnerDaoFlow.component()+"-> "+tmoInnerDaoFlow.component()+" :  "+tmoInnerDaoFlow.componentOperation()+"\n");
		sbInnerDao.append("activate "+tmoInnerDaoFlow.component()+"\n");
		if(tmoInnerDaoFlow.componentCondition()!=null && !tmoInnerDaoFlow.componentCondition().isEmpty() && !tmoInnerDaoFlow.componentCondition().trim().isEmpty()) {						
			sbInnerDao.append("hnote "+this.CONDITION_DIRECTION+" "+tmoInnerDaoFlow.component()+" "+this.NOTE_BG_COLOR+": "+tmoInnerDaoFlow.componentCondition()+"\n");
		}
		sbInnerDao.append(tmoInnerDaoFlow.component()+"-> "+backendInfo.backend()+" :  "+backendInfo.backendOperation()+"\n");
		sbInnerDao.append("activate "+backendInfo.backend()+"\n");
		sbInnerDao.append(backendInfo.backend()+" --> "+tmoInnerDaoFlow.component()+": "+backendInfo.backendOperation()+"\n");
		sbInnerDao.append("deactivate "+backendInfo.backend()+"\n");
		sbInnerDao.append(tmoInnerDaoFlow.component()+" --> "+tmoInnerDaoFlow.component()+": "+((StringUtils.isNotEmpty(tmoInnerDaoFlow.componentOutput()))?tmoInnerDaoFlow.componentOutput():tmoInnerDaoFlow.componentOperation())+"\n");
		sbInnerDao.append("deactivate "+tmoInnerDaoFlow.component()+"\n");
		return sbInnerDao.toString();
	}
	
	private TmoComponentCollection getCompCollection(String comp, TmoComponent annotation) {
		TmoComponentCollection collection = new TmoComponentCollection();
		List<String>compList = new ArrayList<String>();
		List<String>daoList = new ArrayList<String>();
		for(TmoComponentFlow tmoComponentflow:annotation.componentFlows()){
			if(this.tmoDaoMap.containsKey(tmoComponentflow.component()+"#"+tmoComponentflow.componentOperation())){
				daoList.add(tmoComponentflow.component()+"#"+tmoComponentflow.componentOperation());
			}else if(this.tmoComponentMap.containsKey(tmoComponentflow.component()+"#"+tmoComponentflow.componentOperation())){
				compList.add(tmoComponentflow.component()+"#"+tmoComponentflow.componentOperation());
			}
		}
		collection.setComponetCollection(compList);
		collection.setDaoCollection(daoList);
		//System.out.println("For comp : "+comp+" collection : "+collection);
		return collection;
	}

	
	private String createOperationHtml(String operationName, String description) {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"accordion\">"+operationName+"</div>");
		sb.append("<div class=\"panel\"><div style=\"font-size: large; margin: 10px;\"><b><u>Operation Description</u> : </b></div><div style=\" margin: 15px ;font-style: italic\">");
		sb.append(description+"</div>");
		sb.append("<img style=\"margin: 10px;\" src=\"./"+operationName+".svg\" alt=\"Sequence Diagram\" height=\"100%\" width=\"100%\"></div>");
		return sb.toString();
	}

	private void createHtmlFile(RoundEnvironment roundEnv, List<String> tmoDaoHtmlList, List<String> tmoOperationHtmlList) {
		File outputDir=new File(System.getProperty("user.dir")+"\\src\\main\\resources\\static\\docs");
		try {
			writeDirectoryFilesToHTML(outputDir,roundEnv,tmoDaoHtmlList,tmoOperationHtmlList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writeDirectoryFilesToHTML(File directory,RoundEnvironment roundEnv, List<String> tmoDaoHtmlList, List<String> tmoOperationHtmlList) throws IOException {
        ArrayList<String> contents = new ArrayList<String>(Arrays.asList(directory.list()));
        File output = new File(System.getProperty("user.dir")+"\\src\\main\\resources\\static\\docs\\docs.html");
        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        try{
        	writer.write(ProcessorConst.START_HTML);
        	if(serviceName.contains("Impl")) {
        		serviceName=serviceName.replaceAll("Impl", "");
        	}
        	writer.write(serviceName+"</div>");
        	//wsdl
        	if(processingEnv.getOptions().get("wsdl")!=null) {
        		wsdl=processingEnv.getOptions().get("wsdl");
        		if(!wsdl.endsWith("?wsdl")) {
        			if(wsdl.endsWith(".wsdl")) {
        				wsdl=wsdl.replace(".wsdl", "?wsdl");
        			}else if (wsdl.endsWith("service")) {
        				wsdl=wsdl+"?wsdl";
        			}
        		}
        	}else {
        		wsdl="/rsp/"+serviceName+"?wsdl";
        	}
        	if(wsdl!=null) {
        		writer.write("<div style=\"font-size: x-large; margin: 10px;\"><b><u>WSDL</u> : </b></div>");
        		writer.write("<a style=\"margin: 10px;\" href=\""+wsdl+"\" target=\"_blank\">click here to see wsdl</a>");
        	}
        	
			// backends
			writer.write("<div style=\"font-size: x-large; margin: 10px;\"><b><u>Backends</u> : </b></div><div class=\"list-type1\"><ol>");
			System.out.println(tmoDaoHtmlList);
			for(String backend :tmoDaoHtmlList) {
				writer.write(backend);
			}
			writer.write("</ol></div>");
			
			// operations
			writer.write("<div style=\"font-size: x-large; margin: 10px;\"><b><u>Operations</u> : </b></div>");
			System.out.println(tmoOperationHtmlList);
			for (String op : tmoOperationHtmlList) {
				writer.write(op);
			}
            writer.write(ProcessorConst.END_HTML);
        }catch(Exception e) {
        	e.printStackTrace();
        }finally {
        	writer.close();
		}
    	
	}

	private Set<String> managerSet = new LinkedHashSet<String>();
	private Set<String> daoSet = new LinkedHashSet<String>();
	private Set<String> backendSet = new LinkedHashSet<String>();
	private String createParticipants(TmoOperationFlow[] opFlowList) {
		StringBuffer managerParticipants=new StringBuffer();
		StringBuffer daoParticipants=new StringBuffer();
		StringBuffer backendParticipants=new StringBuffer();
		
		if(opFlowList!=null && opFlowList.length>0) {
			daoParticipants.append("box \"Dao\" "+ this.DAO_BACKGROUND_COLOR+" \n");
			managerParticipants.append("box \"Components\" "+ this.MANAGER_BACKGROUND_COLOR+" \n");
			backendParticipants.append("box \"Backend\" "+ this.BACKEND_BACKGROUND_COLOR+" \n");
			// getting components in one operation
			for(TmoOperationFlow opFlow:opFlowList) {
				if(this.tmoDaoMap.containsKey(opFlow.component()+"#"+opFlow.componentOperation())){
					this.daoSet.add(opFlow.component());
					this.backendSet.add(this.tmoDaoMap.get(opFlow.component()+"#"+opFlow.componentOperation()).backend());
				}
			}
			for(TmoOperationFlow opFlow:opFlowList) {
				if(opFlow.component()!=null && !opFlow.component().trim().isEmpty()) {
					if(this.tmoComponentClassList.contains(opFlow.component())) {
						getInnerComponets(opFlow.component()+"#"+opFlow.componentOperation());
					}
				}
			}
			for(String manager:this.managerSet) {						
					managerParticipants.append("participant "+manager+"\n");					
				}			
			
			for(String dao:this.daoSet) {						
				daoParticipants.append("participant "+dao+"\n");					
			}
			for(String backend:this.backendSet) {						
				backendParticipants.append("participant "+backend+"\n");					
			}
			managerParticipants.append("end box\n");
			daoParticipants.append("end box\n");
			backendParticipants.append("end box\n");

		}
		//System.out.println("managerParticipants " +managerParticipants);
		//System.out.println("daoParticipants " +daoParticipants);
		//System.out.println("backendParticipants " +backendParticipants);
		managerParticipants.append(daoParticipants);
		managerParticipants.append(backendParticipants);
		return managerParticipants.toString();
	}

	private void getInnerComponets(String component) {
		if(this.compCollection.containsKey(component)) {
			if(this.compCollection.get(component).getDaoCollection().size()>0) {
				for(String innerDao:this.compCollection.get(component).getDaoCollection()) {
					this.daoSet.add(innerDao.split("#")[0]);
					this.backendSet.add(this.tmoDaoMap.get(innerDao).backend());
				}
			}
			this.managerSet.add(component.split("#")[0]);
			if(this.compCollection.get(component).getComponetCollection().size()>0) {				
				for(String innerComp:this.compCollection.get(component).getComponetCollection()) {
						getInnerComponets(innerComp);
				}
			}
		}
	}


	private void createDiagram(StringBuffer sb, String operation) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Start-create Sequence Diagram");
		try {
			List<String> lines = Arrays.asList(sb.toString());
			Path path = Paths.get(operation+".txt");
			Files.write(path, lines, Charset.forName("UTF-8"));
			File file = path.toFile();			
			file.deleteOnExit();        // This tells JVM to delete the file on JVM exit.
			File outputDir=new File(System.getProperty("user.dir")+"\\src\\main\\resources\\static\\docs");
			if(!outputDir.exists()) {
				outputDir.mkdirs();
			}
			final SourceFileReader sourceFileReader=new SourceFileReader(file,outputDir,new FileFormatOption(FileFormat.SVG));
			for (final GeneratedImage image : sourceFileReader.getGeneratedImages()) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Generated file :-" + image.getDescription());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to create Sequence Diagram");
		}
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "End-create Sequence Diagram");
	}

	private StringBuffer setupTitle(StringBuffer sb, String serviceName, String operationName,String description) {
		//String scale="scale 800 width\n" + "scale 600 height\n";
		String padding="skinparam ParticipantPadding 10\n";
		String s="skinparam sequence {\n"+
				"\tArrowColor #b40d63\n"+
				"\tParticipantBorderColor #E20074\n"+
				" ParticipantBackgroundColor white\n"+
				"\tParticipantFontSize 17\n"+
				" LifeLineBorderColor #b40d63\n"+
				"\tLifeLineBackgroundColor white\t\n"+
				"}";
		//sb.append(scale);
		sb.append(padding);
		sb.append(s);
		sb.append("\n");
		sb.append("title\n");
		sb.append("<font size=\"18\" color=#b40d63>"+serviceName+"."+operationName+"</font>\n");
		if(description!=null &&!description.isEmpty()) {
			sb.append("Description :"+description+"\n");			
		}
		sb.append("end title\n");		
		sb.append("center header\n");
		sb.append("Sequence-Diagram\n");
		sb.append("end header\n");
		sb.append("center footer\n");
		sb.append("<font size=\"10\" >Created on :<i>"+LocalDateTime.now()+"</i></font>\n");
		sb.append("end footer\n");
		return sb;
	}
	private static String getNameFromPackageName(String x) {
		return x.substring(x.lastIndexOf(".") + 1);
	}
}
