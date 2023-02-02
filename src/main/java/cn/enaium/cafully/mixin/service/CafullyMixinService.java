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

package cn.enaium.cafully.mixin.service;

import cn.enaium.cafully.mixin.Mixin;
import cn.enaium.cafully.mixin.TinyLog;
import cn.enaium.cafully.util.LocationUtil;
import com.google.common.collect.ImmutableList;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.ContainerHandleURI;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.transformers.MixinClassReader;
import org.tinylog.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Enaium
 */
public class CafullyMixinService extends MixinServiceAbstract {
    @Override
    public String getName() {
        return "Cafully";
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_8;
    }

    @Override
    public MixinEnvironment.CompatibilityLevel getMaxCompatibilityLevel() {
        return MixinEnvironment.CompatibilityLevel.JAVA_17;
    }

    @Override
    protected ILogger createLogger(String name) {
        return new TinyLog(name);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IContainerHandle getPrimaryContainer() {
        try {
            return new ContainerHandleURI(LocationUtil.getLocation(this.getClass()));
        } catch (URISyntaxException e) {
            Logger.error(e);
        }
        return new ContainerHandleVirtual(this.getName());
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return Collections.emptyList();
    }


    @Override
    public IClassTracker getClassTracker() {
        return null;
    }

    @Override
    public IMixinAuditTrail getAuditTrail() {
        return null;
    }

    @Override
    public ITransformerProvider getTransformerProvider() {
        return null;
    }

    @Override
    public Collection<String> getPlatformAgents() {
        return ImmutableList.of("cn.enaium.cafully.mixin.CafullyPlatformAgentMerger");
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return Mixin.helper.environment().loader.getResourceAsStream(name);
    }

    @Override
    public IClassProvider getClassProvider() {
        return new IClassProvider() {
            @Override
            public URL[] getClassPath() {
                return Mixin.helper.environment().loader.getURLs();
            }

            @Override
            public Class<?> findClass(String name) throws ClassNotFoundException {
                return Mixin.helper.environment().loader.loadClass(name);
            }

            @Override
            public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
                return Class.forName(name, initialize, Mixin.helper.environment().loader);
            }

            @Override
            public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
                return Class.forName(name, initialize, Mixin.helper.environment().loader);
            }
        };
    }

    @Override
    public IClassBytecodeProvider getBytecodeProvider() {
        return new IClassBytecodeProvider() {
            @Override
            public ClassNode getClassNode(String name) throws IOException {
                return getClassNode(name, true);
            }

            @Override
            public ClassNode getClassNode(String name, boolean runTransformers) throws IOException {
                ClassNode classNode = new ClassNode();
                final ClassWriter classWriter = new ClassWriter(ClassReader.EXPAND_FRAMES);
                final InputStream inputStream = Objects.requireNonNull(Mixin.helper.environment().loader.getResourceAsStream(name.replace('.', '/') + ".class"), name);
                new ClassReader(inputStream).accept(classWriter, ClassReader.EXPAND_FRAMES);
                ClassReader classReader = new MixinClassReader(classWriter.toByteArray(), name);
                classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
                return classNode;
            }
        };
    }

}
