package com.techshroom.obf.methodup;

import com.google.inject.AbstractModule;
import com.techshroom.obf.methodup.transformer.TransformerProvider;
import com.techshroom.obf.methodup.transformer.impl.TransformerProviderImpl;

/**
 * Main module for injections.
 * 
 * @author Kenzie Togami
 */
public class MainModule
        extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransformerProvider.class)
                .toInstance(TransformerProviderImpl.INSTANCE);
    }
}
