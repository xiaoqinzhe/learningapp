package com.example.buildsrc.layoutbinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javassist.CtMethod;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Opcode;
import javassist.convert.TransformCall;
import javassist.convert.Transformer;

public class StaticTransformCall extends TransformCall {
    public StaticTransformCall(@Nullable Transformer next, @NotNull CtMethod origMethod, @NotNull CtMethod substMethod) {
        super(next, origMethod, substMethod);
        this.methodDescriptor = origMethod.getMethodInfo2().getDescriptor();
    }

    // todo read
    @Override
    protected int match(int c, int pos, @NotNull CodeIterator iterator, int typedesc, @NotNull ConstPool cp) {
        if (this.newIndex == 0) {
            String desc = Descriptor.insertParameter(this.classname, this.methodDescriptor);
            int nt = cp.addNameAndTypeInfo(this.newMethodname, desc);
            int ci = cp.addClassInfo(this.newClassname);
            this.newIndex = cp.addMethodrefInfo(ci, nt);
            this.constPool = cp;
        }

        iterator.writeByte(Opcode.INVOKESTATIC, pos);
        iterator.write16bit(this.newIndex, pos + 1);

        System.out.println("StaticTransformCall: match! classname ：" + classname);
        return pos;
    }
}
