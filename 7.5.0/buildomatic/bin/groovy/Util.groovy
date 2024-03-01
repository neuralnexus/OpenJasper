/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Various utility methods from the scanner
 */
def test_parsePath() {
    ["aa/bb/cc/dd.e.f.g", "aa/bb/cc/dd.e", "aa/bb/cc/dd", "aa.bb", "abc" ].each { s -> println parsePath(s) }
}

// call closure, stick the milliseconds into t
def benchmark(t, closure) {
    if (! t.class.array) {
        throw new IllegalArgumentException("usage: benchmark(n[], closure)")
    }
    start = System.currentTimeMillis()  
    def ret = closure.call()  
    now = System.currentTimeMillis()  
    t[0] = now - start
    ret
}

// count calls to read() by length of returned buf (some stuff might go through multiple read() calls)
// keep call info in a map
class CountingInputStream extends FilterInputStream {
    def histo = [:]

    CountingInputStream(is) { super(is) }

    def count(len) {
        histo[len] ? histo[len]++ : (histo[len] = 1)
    }
    public int read() throws IOException {
        count(1)
        return super.read();
    }
    public int read(byte[] b) throws IOException {
        count(b.length)
        return super.read(b);
    }
    public int read(byte[] b, int off, int len) throws IOException {
        count(len)
        return super.read(b, off, len);
    }
}


// print out map of maps
def mmap(m, depth = 0) {
    m.each { k, v ->
        if (v instanceof Map) {
            println "${' ' * depth} $k:"
            mmap(v, depth + 4)
        }
        else {
            println "${' ' * depth} $k: $v"
        }
    }
}
