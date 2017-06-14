package com.yahoo.tensor.functions;

import com.google.common.annotations.Beta;
import com.yahoo.tensor.Tensor;

/**
 * A primitive tensor function is a tensor function which cannot be expressed in terms of other tensor functions.
 * All tensor implementations must implement all primitive tensor functions.
 * Primitive tensor functions are fully inspectable.
 * 
 * @author bratseth
 */
@Beta
public abstract class PrimitiveTensorFunction extends TensorFunction {
    
}
