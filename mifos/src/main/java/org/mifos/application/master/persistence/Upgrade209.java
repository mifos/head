package org.mifos.application.master.persistence;

import java.util.List;

public class Upgrade209 extends LanguageUpgrade {

    public Upgrade209() {
		super(209);
	}

    @Override
    public void addData(List<String[]> languageNameAndCodesToAdd) {
      languageNameAndCodesToAdd.add(new String[] { "Portuguese", "pt" });
      languageNameAndCodesToAdd.add(new String[] { "Khmer", "km" });
    }

}
