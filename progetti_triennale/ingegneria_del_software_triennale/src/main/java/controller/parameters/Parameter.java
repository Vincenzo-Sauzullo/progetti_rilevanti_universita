package controller.parameters;

import controller.exceptions.ParameterException;

public interface Parameter {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    void validate() throws ParameterException;
}
