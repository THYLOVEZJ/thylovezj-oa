package com.thylovezj.oa.utils;

import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
    public static String md5Digest(String source){
        return DigestUtils.md5Hex(source);
    }
}
