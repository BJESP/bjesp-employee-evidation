package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class KeyStoreConfig {
    @Value("${ks.filename}")
    private String FileName;

    @Value("${ks.alias}")
    private String alias;

    @Value("${ks.password}")
    private String ksPassword;


    @Value("${pikulaKS.filename}")
    private String FileNamePikula;

    @Value("${pikulaKS.alias}")
    private String aliasPikula;

    @Value("${pikulaKS.password}")
    private String ksPasswordPikula;


    public KeyStoreConfig() {
    }

    public String getFileName() {
        return FileName;
    }
    public String getPikulaFileName() {
        return FileNamePikula;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getAlias() {
        return alias;
    }
    public String getPikulaAlias() {
        return aliasPikula;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getKsPassword() {
        return ksPassword;
    }


    public String getPikulaKsPassword() {
        return ksPasswordPikula;
    }

    public void setKsPassword(String ksPassword) {
        this.ksPassword = ksPassword;
    }
}
