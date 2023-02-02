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

import org.spongepowered.asm.launch.platform.CommandLineOptions;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

public final class MixinBootstrap {
    private static Object transformer;
    private static Method method;

    private static boolean init = false;

    public static void init() throws Exception {
        if (!init) {
            final Class<?> bootstrap = Class.forName("org.spongepowered.asm.launch.MixinBootstrap");
            getMethod(bootstrap, "start").invoke(null);
            getMethod(bootstrap, "doInit", CommandLineOptions.class).invoke(null, CommandLineOptions.of(Collections.emptyList()));
            getMethod(bootstrap, "inject").invoke(null);
            getMethod(MixinEnvironment.class, "gotoPhase", MixinEnvironment.Phase.class).invoke(null, MixinEnvironment.Phase.DEFAULT);

            Class<?> klass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            Constructor<?> c = klass.getDeclaredConstructor();
            c.setAccessible(true);
            transformer = c.newInstance();
            method = getMethod(klass, "transformClassBytes", String.class, String.class, byte[].class);

            init = true;
        }
    }

    public static byte[] transformClass(String name, byte[] bytes) {
        try {
            return (byte[]) method.invoke(transformer, name, name, bytes);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getMethod(Class<?> klass, String name, Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException {
        final Method declaredMethod = klass.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
