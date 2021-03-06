/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.docx4j.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.docx4j.Docx4jProperties;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.template.utils.ArrayUtils;
import org.docx4j.template.utils.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

/**
 * 
 * @className	： WordprocessingMLThymeleafTemplate
 * @description	： 该模板仅负责使用Thymeleaf模板引擎将指定模板生成HTML并将HTML转换成XHTML后，作为模板生成WordprocessingMLPackage对象
 * @author 		： <a href="https://github.com/vindell">vindell</a>
 * @date		： 2017年5月24日 下午10:31:08
 * @version 	V1.0
 */
public class WordprocessingMLThymeleafTemplate extends WordprocessingMLTemplate {
	
	protected TemplateEngine engine;
	protected AbstractConfigurableTemplateResolver templateResolver;
	protected String templateKey;
	protected boolean altChunk = false ;

	public WordprocessingMLThymeleafTemplate(String template,boolean altChunk) {
		this.templateKey = template;
		this.altChunk = altChunk;
	}

	@Override
	public WordprocessingMLPackage process(Map<String, Object> variables) throws Exception {
		// 创建模板输出内容接收对象
		StringWriter output = new StringWriter();
		//设置上下文参数
		Context ctx = new Context();
        ctx.setVariables(variables);
		// 使用Thymeleaf模板引擎渲染模板
		getEngine().process(templateKey , ctx , output);
		//获取模板渲染后的结果
		String html = output.toString();
		//使用HtmlTemplate进行渲染
		return new WordprocessingMLHtmlTemplate(html , altChunk).process(variables);
	}
	
	public TemplateEngine getEngine() throws IOException {
		return engine == null ? getInternalEngine() : engine;
	}

	public void setEngine(TemplateEngine engine) {
		this.engine = engine;
	}
	
	protected TemplateEngine getInternalEngine() throws IOException{
		//初始化模板解析器
		AbstractConfigurableTemplateResolver templateResolver =  getTemplateResolver();
		if( getTemplateResolver() == null){
			String resolver = Docx4jProperties.getProperty("docx4j.thymeleaf.templateResolver","org.thymeleaf.templateresolver.FileTemplateResolver");
			if("org.thymeleaf.templateresolver.FileTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new FileTemplateResolver();
			}else if("org.thymeleaf.templateresolver.ClassLoaderTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new ClassLoaderTemplateResolver();
			}else if("org.thymeleaf.templateresolver.UrlTemplateResolver".equalsIgnoreCase(resolver)){
				templateResolver = new UrlTemplateResolver();
			}else{
				templateResolver = new FileTemplateResolver();
			}
		}
		templateResolver.setCacheable(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheable", true));
		templateResolver.setCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.cacheablePatterns", ""))));
		String cacheTTLMs = Docx4jProperties.getProperty("docx4j.thymeleaf.cacheTTLMs");
		templateResolver.setCacheTTLMs( cacheTTLMs == null ? null : Long.valueOf(cacheTTLMs)); 
		templateResolver.setCharacterEncoding(Docx4jProperties.getProperty("docx4j.thymeleaf.charset","UTF-8"));
		templateResolver.setCheckExistence(Docx4jProperties.getProperty("docx4j.thymeleaf.checkExistence", false ));
		templateResolver.setCSSTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newCSSTemplateModePatterns", ""))));
		templateResolver.setHtmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newHtmlTemplateModePatterns", ""))));
		templateResolver.setJavaScriptTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newJavaScriptTemplateModePatterns", ""))));
		templateResolver.setName(Docx4jProperties.getProperty("docx4j.thymeleaf.name",templateResolver.getClass().getName()));
		templateResolver.setNonCacheablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.nonCacheablePatterns", ""))));
		templateResolver.setOrder(Integer.valueOf(Docx4jProperties.getProperty("docx4j.thymeleaf.order","1")));
		templateResolver.setPrefix(Docx4jProperties.getProperty("docx4j.thymeleaf.prefix"));
		templateResolver.setRawTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newRawTemplateModePatterns", ""))));
		templateResolver.setResolvablePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.resolvablePatterns", ""))));
		templateResolver.setSuffix(Docx4jProperties.getProperty("docx4j.thymeleaf.suffix",".tpl"));
		//templateResolver.setTemplateAliases(templateAliases);
		templateResolver.setTemplateMode(Docx4jProperties.getProperty("docx4j.thymeleaf.templateMode","XHTML"));
		templateResolver.setTextTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newTextTemplateModePatterns", ""))));
		templateResolver.setUseDecoupledLogic(Docx4jProperties.getProperty("docx4j.thymeleaf.useDecoupledLogic", false ));
		templateResolver.setXmlTemplateModePatterns(ArrayUtils.asSet(StringUtils.tokenizeToStringArray(Docx4jProperties.getProperty("docx4j.thymeleaf.newXmlTemplateModePatterns", ""))));
        //初始化引擎对象
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        //调用getConfiguration初始化引擎
        engine.getConfiguration();
        return engine;
	}

	public AbstractConfigurableTemplateResolver getTemplateResolver() {
		return templateResolver;
	}

	public void setTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {
		this.templateResolver = templateResolver;
	}
	
}
