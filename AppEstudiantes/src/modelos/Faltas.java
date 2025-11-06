package modelos;

public class Faltas {
	private String numControl;
	private int faltasLengua;
	private int faltasHumanidades;
	private int faltasMatematicas;
	private int faltasSociales;
	private int faltasCiencias;

	public Faltas(String numControl, int faltasLengua, int faltasHumanidades, int faltasMatematicas,
			int faltasSociales, int faltasCiencias) {
		this.numControl = numControl;
		this.faltasLengua = faltasLengua;
		this.faltasHumanidades = faltasHumanidades;
		this.faltasMatematicas = faltasMatematicas;
		this.faltasSociales = faltasSociales;
		this.faltasCiencias = faltasCiencias;
	}

	// Getters y Setters
	public String getNumControl() {
		return numControl;
	}

	public void setNumControl(String numControl) {
		this.numControl = numControl;
	}

	public double getFaltasLengua() {
		return faltasLengua;
	}

	public void setFaltasLengua(int faltasLengua) {
		this.faltasLengua = faltasLengua;
	}

	public double getFaltasHumanidades() {
		return faltasHumanidades;
	}

	public void setFaltasHumanidades(int faltasHumanidades) {
		this.faltasHumanidades = faltasHumanidades;
	}

	public double getFaltasMatematicas() {
		return faltasMatematicas;
	}

	public void setFaltasMatematicas(int faltasMatematicas) {
		this.faltasMatematicas = faltasMatematicas;
	}

	public double getFaltasSociales() {
		return faltasSociales;
	}

	public void setFaltasSociales(int faltasSociales) {
		this.faltasSociales = faltasSociales;
	}

	public double getFaltasCiencias() {
		return faltasCiencias;
	}

	public void setFaltasCiencias(int faltasCiencias) {
		this.faltasCiencias = faltasCiencias;
	}
}
