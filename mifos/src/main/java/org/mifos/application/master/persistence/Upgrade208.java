package org.mifos.application.master.persistence;

import java.util.List;

public class Upgrade208 extends LanguageUpgrade {

    public Upgrade208() {
		super(208);
	}

    @Override
    public void addData(List<String[]> languageNameAndCodesToAdd) {
        languageNameAndCodesToAdd.add(new String[] { "Chinese", "zh" });
        languageNameAndCodesToAdd.add(new String[] { "Swahili", "sw" });
        languageNameAndCodesToAdd.add(new String[] { "Arabic", "ar" });
    }

}
