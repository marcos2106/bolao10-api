package br.com.segmedic.clubflex.processaux;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MaskFormatter;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.segmedic.clubflex.domain.Dependent;
import br.com.segmedic.clubflex.service.DependentService;

@Component
public class fixCellPhoneDependents {

	@Autowired
	DependentService dependentService;

	private MaskFormatter mf;

	@Transactional
	public void fixLengthCellPhoneNumbers() {

		List<Dependent> dependents = new ArrayList<Dependent>();
		dependents = dependentService.listAllDependents();
		String phone = "";
		String ddd = "";
		String phoneNumber = "";

		int i = 0;
		for (Dependent dependent : dependents) {

			if (dependent.getPhone() != null && dependent.getPhone().length() == 13) {

				if ((dependent.getPhone().indexOf("(") > -1) && (dependent.getPhone().indexOf(")") > -1)) {
					try {
						ddd = dependent.getPhone().substring(dependent.getPhone().indexOf("("),
								dependent.getPhone().indexOf(")") + 1);
						phone = dependent.getPhone().substring(dependent.getPhone().indexOf(")") + 1,
								dependent.getPhone().length());
						if (phone.startsWith("9") || phone.startsWith("8")) {
							mf = new MaskFormatter("(##)#####-####");
							mf.setValueContainsLiteralCharacters(false);
							phoneNumber = (ddd).concat("9").concat(phone).replaceAll("[^0-9]", "");
							phoneNumber = mf.valueToString(phoneNumber);
							dependent.setPhone(phoneNumber);
							dependentService.update(dependent);
							System.out.println("telefone antigo: " + ddd + phone);
							System.out.println("telefone corrigido: " + dependent.getPhone());
							i++;
						}else {
							System.out.println(dependent.getPhone());
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}

		System.out.println("Total: " + i);
	}

}
