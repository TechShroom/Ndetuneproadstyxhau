package com.techshroom.obf.methodup.transformer.impl;

import java.nio.file.Path;

import com.techshroom.obf.methodup.transformer.Transformer;
import com.techshroom.obf.methodup.transformer.TransformerProvider;

@SuppressWarnings("javadoc")
public enum TransformerProviderImpl implements TransformerProvider {
    
    INSTANCE;

    @Override
    public Transformer getDirectoryTransformer(Path input, Path output) {
        return new DirectoryTransformer(input, output);
    }

}
