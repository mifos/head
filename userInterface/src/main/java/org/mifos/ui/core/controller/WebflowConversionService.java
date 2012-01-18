package org.mifos.ui.core.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Component("webflowAndMvcConversionService")
public class WebflowConversionService extends FormattingConversionServiceFactoryBean {

    private static final boolean JODATIMEPRESENT = ClassUtils.isPresent(
            "org.joda.time.LocalDate", WebflowConversionService.class.getClassLoader());

    /**
     * Install Formatters and Converters into the new FormattingConversionService using the FormatterRegistry SPI.
     * Subclasses may override to customize the set of formatters and/or converters that are installed.
     */
    @Override
    protected void installFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());
        if (JODATIMEPRESENT) {
            new JodaTimeFormatterRegistrar().registerFormatters(registry);
        }
        else {
            registry.addFormatterForFieldAnnotation(new NoJodaDateTimeFormatAnnotationFormatterFactory());
        }
    }

    /**
     * Dummy AnnotationFormatterFactory that simply fails if @DateTimeFormat is being used
     * without the JodaTime library being present.
     */
    private static final class NoJodaDateTimeFormatAnnotationFormatterFactory
            implements AnnotationFormatterFactory<DateTimeFormat> {

        private final Set<Class<?>> fieldTypes;

        public NoJodaDateTimeFormatAnnotationFormatterFactory() {
            Set<Class<?>> rawFieldTypes = new HashSet<Class<?>>(4);
            rawFieldTypes.add(Date.class);
            rawFieldTypes.add(Calendar.class);
            rawFieldTypes.add(Long.class);
            this.fieldTypes = Collections.unmodifiableSet(rawFieldTypes);
        }

        @Override
        public Set<Class<?>> getFieldTypes() {
            return this.fieldTypes;
        }

        @Override
        public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
            throw new IllegalStateException("JodaTime library not available - @DateTimeFormat not supported");
        }

        @Override
        public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
            throw new IllegalStateException("JodaTime library not available - @DateTimeFormat not supported");
        }
    }
}