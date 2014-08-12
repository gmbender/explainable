/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Gabriel Bender
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

package com.github.explainable.sql.pipeline;

import com.github.explainable.sql.pipeline.passes.ViewExtractionPipeline;
import net.sf.jsqlparser.statement.select.Select;

/**
 * An analysis pipeline that applies some {@link TransformationPass}s to the abstract syntax tree of
 * a SQL query and then runs an {@link OutputPass} to obtain and return an output value of type
 * {@code T}. Pipeline construction should be handled by {@link PipelineBuilder} which does some
 * extra work to make sure that dependencies between the different pipeline stages are satisfied.
 * See {@link ViewExtractionPipeline} for an example of how this interface should be used.
 */
public interface Pipeline<T> {
	T execute(Select select);
}
