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

import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

/**
 * @author Enaium
 */
public class TinyLog extends LoggerAdapterAbstract {
    private final TaggedLogger logger;

    public TinyLog(String name) {
        super(name);
        logger = Logger.tag(name);
    }

    @Override
    public String getType() {
        return "TinyLog(via Cafully)";
    }

    @Override
    public void catching(Level level, Throwable t) {
        logger.error(t);
    }

    @Override
    public void catching(Throwable t) {
        logger.error(t);
    }

    @Override
    public void debug(String message, Object... params) {
        logger.debug(message, params);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.info(message);
        logger.debug(t);
    }

    @Override
    public void error(String message, Object... params) {
        logger.error(message, params);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.info(message);
        logger.error(t);
    }

    @Override
    public void fatal(String message, Object... params) {
        logger.error(message, params);
    }

    @Override
    public void fatal(String message, Throwable t) {
        logger.info(message);
        logger.error(t);
    }

    @Override
    public void info(String message, Object... params) {
        logger.info(message, params);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message);
        logger.info(t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        logger.info(message, params);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        logger.error(t);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        logger.error(t);
        return t;
    }

    @Override
    public void trace(String message, Object... params) {
        logger.trace(message, params);
    }

    @Override
    public void trace(String message, Throwable t) {
        logger.info(message);
        logger.trace(t);
    }

    @Override
    public void warn(String message, Object... params) {
        logger.warn(message, params);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.info(message);
        logger.warn(t);
    }
}
