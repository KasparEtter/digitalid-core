package net.digitalid.core.subject.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.type.DeclaredType;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.circumfixes.Brackets;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.generator.annotations.meta.Interceptor;
import net.digitalid.utility.generator.information.method.MethodInformation;
import net.digitalid.utility.generator.information.type.TypeInformation;
import net.digitalid.utility.processing.logging.ProcessingLog;
import net.digitalid.utility.processing.utility.ProcessingUtility;
import net.digitalid.utility.processor.generator.JavaFileGenerator;
import net.digitalid.utility.string.Strings;
import net.digitalid.utility.validation.annotations.type.Stateless;

import net.digitalid.database.subject.annotations.GenerateSubjectModule;

import net.digitalid.core.subject.CoreSubject;
import net.digitalid.core.subject.CoreSubjectModule;
import net.digitalid.core.subject.CoreSubjectModuleBuilder;

/**
 * This method interceptor generates a core subject module with the service, the index, with which factory an instance of the type is built, and its converter.
 * 
 * @see GenerateSynchronizedProperty
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(GenerateCoreSubjectModule.Interceptor.class)
public @interface GenerateCoreSubjectModule {
    
    /**
     * This class generates the interceptor for the surrounding annotation.
     */
    @Stateless
    public static class Interceptor extends GenerateSubjectModule.Interceptor {
        
        @Pure
        @Override
        public void generateFieldsRequiredByMethod(@Nonnull JavaFileGenerator javaFileGenerator, @Nonnull MethodInformation method, @Nonnull TypeInformation typeInformation) {
            final @Nullable DeclaredType subjectType = ProcessingUtility.getSupertype(typeInformation.getType(), CoreSubject.class);
            if (subjectType == null) { ProcessingLog.error("The type $ is not a subtype of CoreSubject.", ProcessingUtility.getQualifiedName(typeInformation.getType())); }
            final @Nonnull FiniteIterable<@Nonnull String> types = FiniteIterable.of(subjectType.getTypeArguments()).combine(FiniteIterable.of(typeInformation.getType())).map(javaFileGenerator::importIfPossible).evaluate();
            
            javaFileGenerator.addField("static final @" + javaFileGenerator.importIfPossible(Nonnull.class) + " " + javaFileGenerator.importIfPossible(CoreSubjectModule.class) + types.join(Brackets.POINTY) + " MODULE = " + javaFileGenerator.importIfPossible(CoreSubjectModuleBuilder.class) + "." + types.join(Brackets.POINTY) + "withService(SERVICE).withSubjectFactory" + Brackets.inRound(typeInformation.getSimpleNameOfGeneratedSubclass() + "::new") + ".withEntityConverter" + Brackets.inRound(javaFileGenerator.importIfPossible("net.digitalid.core.entity." + Strings.substringUntilFirst(types.get(0), '<') + "Converter") + ".INSTANCE") + ".withCoreSubjectConverter" + Brackets.inRound(typeInformation.getSimpleNameOfGeneratedConverter() + ".INSTANCE") + ".build()");
        }
        
    }
    
}