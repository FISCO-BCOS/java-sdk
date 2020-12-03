/*
 * Copyright 2014-2020  [fisco-dev]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package org.fisco.bcos.sdk.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DataCompress {
    public static byte[] compress(String data) {

        Deflater compress = new Deflater();

        compress.setInput(data.getBytes());

        byte[] compressedData = new byte[data.length()];
        compress.finish();

        int compressLength = compress.deflate(compressedData, 0, compressedData.length);
        return Arrays.copyOfRange(compressedData, 0, compressLength);
    }

    public static byte[] uncompress(byte[] compressedData) throws IOException, DataFormatException {

        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedData);

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length)) {
            byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }

            return bos.toByteArray();
        }
    }
}
