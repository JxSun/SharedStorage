/*
 * Copyright (c) 2015 Kurt Mbanje
 * Modifications copyright (C) 2017 Kuan-Yu Sun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jxsun.sharedstorage.processor;

import com.google.auto.service.AutoService;
import com.jxsun.sharedstorage.annotation.ModelField;
import com.jxsun.sharedstorage.annotation.SharedModel;
import com.jxsun.sharedstorage.processor.generator.DatabaseGenerator;
import com.jxsun.sharedstorage.processor.generator.Generator;
import com.jxsun.sharedstorage.processor.generator.ProviderGenerator;
import com.jxsun.sharedstorage.processor.generator.TableGenerator;
import com.jxsun.sharedstorage.processor.model.Field;
import com.jxsun.sharedstorage.processor.model.Model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class SharedStorageProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(SharedModel.class.getCanonicalName());
        annotations.add(ModelField.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<Model> models = new ArrayList<>();

        parseModels(roundEnv, models);

        Generator providerGenerator = new ProviderGenerator(models);
        providerGenerator.generate();

        Generator databaseGenerator = new DatabaseGenerator(models);
        databaseGenerator.generate();

        Generator tableGenerator = new TableGenerator(models);
        tableGenerator.generate();

        return false;
    }

    private void parseModels(RoundEnvironment environment, List<Model> models) {
        Set<? extends Element> annotatedElements = environment.getElementsAnnotatedWith(SharedModel.class);
        for (Element element : annotatedElements) {
            List<Field> enclosedFields = new ArrayList<>();
            for (Element classElement : element.getEnclosedElements()) {
                if (classElement.getKind() == ElementKind.FIELD) {
                    ModelField fieldAnnotation = classElement.getAnnotation(ModelField.class);
                    if (fieldAnnotation != null) {
                        Field field = new Field(fieldAnnotation, classElement);
                        enclosedFields.add(field);
                    }
                }
            }

            models.add(new Model(mElementUtils, element, enclosedFields));
        }
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void warn(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.WARNING, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        mMessager.printMessage(kind, message, element);
    }
}
