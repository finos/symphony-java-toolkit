package example.symphony.demoworkflow.expenses;

import javax.validation.constraints.Min;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(name = "New Claim Details")
public class StartClaim {

	String description;
	
	@Min(0)
	Number amount;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}
	
	
}
