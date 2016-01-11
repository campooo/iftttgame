package org.campooo.api.module;

/**
 * ckb on 15/12/1.
 */
public interface Module<T extends ModuleBox> {

    void initialize(T box);

    void destroy();

}

