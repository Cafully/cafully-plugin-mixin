/*
 * Copyright (c) 2023 Enaium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.enaium.cafully.mixin;

import cn.enaium.cafully.plugin.annotation.Dependency;
import cn.enaium.cafully.plugin.annotation.Plugin;
import cn.enaium.cafully.plugin.api.IInitializer;
import cn.enaium.cafully.plugin.api.ITransformer;
import cn.enaium.cafully.plugin.helper.IHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Mixins;

import java.security.ProtectionDomain;

/**
 * @author Enaium
 */
@Plugin(unique = "mixin", name = "Mixin", version = "8.5", api = ">=1.0.0", description = "https://github.com/SpongePowered/Mixin", dependencies = @Dependency(unique = "asm", version = "*"))
public class Mixin implements IInitializer {
    public static IHelper helper;

    @Override
    public void initialize(IHelper helper) throws Throwable {
        Mixin.helper = helper;
    }

    @Override
    public void after() throws Throwable {
        MixinBootstrap.init();

        for (IHelper allHelper : helper.plugin().all()) {
            final String name = allHelper.plugin().annotation().unique() + ".mixin.json";
            if (helper.environment().loader.getResourceAsStream(name) != null) {
                Mixins.addConfiguration(name);
            }
        }

        helper.transformer().add(new ITransformer() {
            @Override
            public boolean supportClass(String s) {
                return true;
            }

            @Override
            public byte[] before(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] basic) throws Exception {
                final String name = (className != null ? className : classBeingRedefined.getName()).replace("/", ".");
                final ClassReader classReader = new ClassReader(basic);
                final ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                basic = MixinBootstrap.transformClass(name, basic);
                return basic;
            }
        });
    }
}
