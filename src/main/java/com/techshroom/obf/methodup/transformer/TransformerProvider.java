package com.techshroom.obf.methodup.transformer;

import java.nio.file.Path;

/**
 * A provider for giving out specialized transformers.
 * 
 * @author Kenzie Togami
 */
public interface TransformerProvider {

    /**
     * Creates a new directory transformer for the given input and output.
     * 
     * @param input
     *            - The input directory
     * @param output
     *            - The output directory
     * @return A transformer for the given directories
     */
    Transformer getDirectoryTransformer(Path input, Path output);

}
