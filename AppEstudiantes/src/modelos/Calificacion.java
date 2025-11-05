package modelos;

public class Calificacion {
	private String numControl;
	private double calfLengua;
	private double calfHumanidades;
	private double calfMatematicas;
	private double calfSociales;
	private double calfCiencias;

	public Calificacion(String numControl, double calfLengua, double calfHumanidades, double calfMatematicas,
			double calfSociales, double calfCiencias) {
		this.numControl = numControl;
		this.calfLengua = calfLengua;
		this.calfHumanidades = calfHumanidades;
		this.calfMatematicas = calfMatematicas;
		this.calfSociales = calfSociales;
		this.calfCiencias = calfCiencias;
	}

	// Getters y Setters
	public String getNumControl() {
		return numControl;
	}

	public void setNumControl(String numControl) {
		this.numControl = numControl;
	}

	public double getCalfLengua() {
		return calfLengua;
	}

	public void setCalfLengua(double calfLengua) {
		this.calfLengua = calfLengua;
	}

	public double getCalfHumanidades() {
		return calfHumanidades;
	}

	public void setCalfHumanidades(double calfHumanidades) {
		this.calfHumanidades = calfHumanidades;
	}

	public double getCalfMatematicas() {
		return calfMatematicas;
	}

	public void setCalfMatematicas(double calfMatematicas) {
		this.calfMatematicas = calfMatematicas;
	}

	public double getCalfSociales() {
		return calfSociales;
	}

	public void setCalfSociales(double calfSociales) {
		this.calfSociales = calfSociales;
	}

	public double getCalfCiencias() {
		return calfCiencias;
	}

	public void setCalfCiencias(double calfCiencias) {
		this.calfCiencias = calfCiencias;
	}
}
