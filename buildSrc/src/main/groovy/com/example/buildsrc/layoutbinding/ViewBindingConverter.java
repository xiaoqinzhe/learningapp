package com.example.buildsrc.layoutbinding;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class ViewBindingConverter {

    public static boolean needChangedViewBinding(CtClass ctClass, ClassPool classPool) {
        // getInterface androidx.viewbinding.ViewBinding
        String name = ctClass.getName();
        String[] parts = name.split("\\.");
        if (parts.length > 1 && parts[parts.length - 2].equals("databinding")
                && name.endsWith("Binding")) {
            String vbName = parts[parts.length - 1];
            String clzName = vbName.substring(0, vbName.length() - 7);
            String lbClz = "com.example.generated.layout." + clzName + "LayoutBinding";
            CtClass ctClass1 = classPool.getOrNull(lbClz);
            return ctClass1 != null;
        }
        return false;
    }

    public static void convert(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        String srcName = ctClass.getName();
        String bindingName = "com.example.generated.layout."
                + srcName.substring(srcName.lastIndexOf(".") + 1, srcName.length() - 7)
                + "LayoutBinding";

        removeBindMethod(ctClass, classPool);
        updateInflateMethod(ctClass, classPool, bindingName);
    }

    private static void removeBindMethod(CtClass ctClass, ClassPool classPool) throws NotFoundException {
        CtMethod ctMethod = ctClass.getDeclaredMethod("bind",
                new CtClass[]{classPool.getCtClass("android.view.View")});
        ctClass.removeMethod(ctMethod);
    }

    private static void updateInflateMethod(CtClass ctClass, ClassPool classPool, String bindingName) throws NotFoundException, CannotCompileException {
        CtClass inflater = classPool.get("android.view.LayoutInflater");
        CtClass viewGroup = classPool.get("android.view.ViewGroup");
        CtClass booleanClz = classPool.get("boolean");
        CtMethod inflateMethod = ctClass.getDeclaredMethod("inflate",
                new CtClass[]{inflater, viewGroup, booleanClz});

        CtConstructor[] ctConstructors = ctClass.getDeclaredConstructors();
        CtConstructor constructors = ctConstructors[0];
        List<String> params = getMethodParameterName(constructors);

        StringBuilder sb = new StringBuilder("{" +
                bindingName + " binding = new " + bindingName + "();" +
                "return new " + ctClass.getName() + "(");
        for (int i = 0; i < params.size(); ++i) {
            System.out.println(params.get(i));
            sb.append("binding.").append(params.get(i));
            if (i != params.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(");}");
        System.out.println(sb.toString());
        inflateMethod.setBody(sb.toString());
    }

    static List<String> getMethodParameterName(CtConstructor ctMethod) throws NotFoundException {
        List<String> names = new ArrayList<>();

        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attributeInfo = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

        int shift = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
//        System.out.println(shift);
//        System.out.println(ctMethod.getParameterTypes().length);
        for (int i = 0; i < ctMethod.getParameterTypes().length; ++i) {
            names.add(attributeInfo.variableName(i + shift));
        }

        return names;
    }

}
