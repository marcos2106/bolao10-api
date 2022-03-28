package br.com.bolao.bolao10.processaux;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.MaskFormatter;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.bolao.bolao10.domain.Holder;
import br.com.bolao.bolao10.service.HolderService;

@Component
public class fixCellPhoneUsers {

	@Autowired
	HolderService holderService;

	private MaskFormatter mf;

	@Transactional
	public void fixLengthCellPhoneNumbers() {

		List<Holder> holders = new ArrayList<Holder>();
		holders = holderService.getAllHolders();
		String phone = "";
		String ddd = "";
		String phoneNumber = "";

		int i = 0;
		for (Holder holder : holders) {

			if (holder.getCellPhone() != null && holder.getCellPhone().length() == 13) {

				if ((holder.getCellPhone().indexOf("(") > -1) && (holder.getCellPhone().indexOf(")") > -1)) {
					try {
						ddd = holder.getCellPhone().substring(holder.getCellPhone().indexOf("("),
								holder.getCellPhone().indexOf(")") + 1);
						phone = holder.getCellPhone().substring(holder.getCellPhone().indexOf(")") + 1,
								holder.getCellPhone().length());
						if (phone.startsWith("9") || phone.startsWith("8")) {
							mf = new MaskFormatter("(##)#####-####");
							mf.setValueContainsLiteralCharacters(false);
							phoneNumber = (ddd).concat("9").concat(phone).replaceAll("[^0-9]", "");
							phoneNumber = mf.valueToString(phoneNumber);
							holder.setCellPhone(phoneNumber);
							holderService.update(holder);
							System.out.println("telefone antigo: " + ddd + phone);
							System.out.println("telefone corrigido: " + holder.getCellPhone());
							i++;
						}else {
							System.out.println(holder.getCellPhone());
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
