package hn.unah.bases.proyecto_playstore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hn.unah.bases.proyecto_playstore.DTOs.CampoDTO;
import hn.unah.bases.proyecto_playstore.DTOs.ConexionDTO;


@SpringBootApplication
public class ProyectoPlaystoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoPlaystoreApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		try (Scanner input = new Scanner(System.in)) {

			/* -------------------------------------------------------- */
			/* ORIGEN DE DATOS */
			/* -------------------------------------------------------- */

			// INICIO DEL PROYECTO
			/** SOLICITAR LA SELECCIÓN DE LA CONEXIÓN DE INGRESO */

			System.out.println("\n\t\t  ╭──────────────────╮");
			System.out.println("\t\t  │                  │");
			System.out.println("\t\t  │  Proyecto ETL    │");
			System.out.println("\t\t  │    IS-601        │");
			System.out.println("\t\t  │                  │");
			System.out.println("\t\t  ╰──────────────────╯");
			System.out.println("\t\t        \\");
			System.out.println("\t\t         \\");
			System.out.println("\t\t          \\");
			System.out.println("\t\t            \\    ^__^");
			System.out.println("\t\t             \\   (oo)\\_______");
			System.out.println("\t\t                (__)\\       )\\/\\");
			System.out.println("\t\t                    ||----w |");
			System.out.println("\t\t                    ||     ||");
			System.out.println("\n\t\t    ¡Bienvenido!");

			System.out.println("╔═════════════════════════════════════════════════════════════╗");
			System.out.println("║                                                             ║");
			System.out.println("║                    Escogiendo el origen de datos            ║");
			System.out.println("║                                                             ║");
			System.out.println("╚═════════════════════════════════════════════════════════════╝");
			System.out.println("\n\n");

			// INICIAREMOS UN WHILE MIENTRAS EL USUARIO QUIERA SEGUIR CREANDO EL ETL
			/**
			 * 0. Crear proceso ETL
			 * 1. Terminar creación de procesos
			 */
			boolean crear = true;

			// CREAMOS LA ESTRUCTURA NECESARIA PARA ALMACENAR LAS CONEXIONES
			ConexionesDisponibles conexionesDisponibles = new ConexionesDisponibles();
			ArrayList<ConexionDTO> conexiones = new ArrayList<ConexionDTO>();

			while (crear) {

				/**
				 * MOSTRAMOS AL USUARIO LAS CONEXIONES DISPONIBLES Y LE PEDIMOS SELECCIONAR
				 * LA CONEXIÓN DE ORIGEN DEL ETL
				 */

				// OBTENEMOS LAS CONEXIONES DISPONIBLES

				conexiones = conexionesDisponibles.obtenerConexionesDisponibles();
				System.out.println(
						"\nA continuación se muestran las conexiones disponibles :");

				// RECORREMOS LA ESTRCUTURA Y LA MOSTRAMOS AL USUARIO
				int i = 0;
				for (ConexionDTO conexionDTO : conexiones) {
					System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
					i++;
				}

				// USUARIO SELECCIONA LA CONEXION
				// VERIFICAR QUE EL VALOR INGRESADO ESTE DENTRO DEL RANGO PERMITIDO
				int conexionSelec = 0;
				boolean seleccionCorrecta = false;

				while (!seleccionCorrecta) {
					System.out.println("\nFavor seleccione una conexión como origen de datos:");
					conexionSelec = input.nextInt();

					// Verificar si el valor ingresado está dentro del rango permitido
					if (conexionSelec >= 0 && conexionSelec < conexiones.size()) {
						seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a true para
													// salir del bucle
					} else {
						System.out.println(
								"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
					}

					// Consumir el salto de línea pendiente después de nextInt() para evitar
					// problemas con las siguientes entradas
					input.nextLine();
				} // WHILE DE INGRESO DE VALOR NUMERICO

				boolean credencialesCorrectas = false;
				ConexionDTO conexionOrigen = new ConexionDTO();
				String contraseniaOrigen = null;
				Conexion conexion = new Conexion();
				Connection conOrigen = null;

				while (!credencialesCorrectas) {
					// Solicitamos las credenciales de la conexión
					conexionOrigen = conexiones.get(conexionSelec);
					System.out.printf("Escriba la contraseña del usuario %s: ", conexionOrigen.getUsername());
					contraseniaOrigen = input.next();

					// Intentamos establecer la conexión

					try {
						conOrigen = conexion.openConnection(conexionOrigen.getUsername(), contraseniaOrigen);
						credencialesCorrectas = true; // Si la conexión es exitosa, establecemos la bandera a true para
														// salir del bucle

					} catch (SQLException e) {
						System.out.println("No se pudo establecer la conexión. Verifique las credenciales.");
						credencialesCorrectas = false;

					}

				} // WHILE DE INTENTO DE CONEXION

				/**
				 * SI LA CONEXION ES EXITOSA OFRECEMOS AL USUARIO OBTENER CAMPOS MEDIANTE UNA
				 * CONSULTA
				 * O ESCOGER UNA TABLA
				 */
				seleccionCorrecta = false;
				int opcion = 0;
				String from = null;
				while (!seleccionCorrecta) {
					System.out.println("╭──────────────────────╮");
					System.out.println("│                      │");
					System.out.println("│         Menú         │");
					System.out.println("│                      │");
					System.out.println("│ 1. Escoger tabla     │");
					System.out.println("│ 2. Ingresar consulta │");
					System.out.println("│                      │");
					System.out.println("│ Ingrese el número    │");
					System.out.println("│ correspondiente a la │");
					System.out.println("│ opción deseada:      │");
					System.out.println("│                      │");
					System.out.println("╰──────────────────────╯");
					opcion = input.nextInt();

					if (opcion == 1 || opcion == 2) {
						seleccionCorrecta = true;
					}

					// Limpiar el salto de línea pendiente después de nextInt()
					input.nextLine();
				}

				// VALOR DE LA TABLA A SELECCIONAR
				int tableSelect = 0;

				// CONSULTA PARA OBTENER INFORAMCIÓN
				String consulta = null;

				// CREAMOS LA ESTRUCTURA PARA OBETENER TODAS LAS TABLAS DE UNA CONEXION
				TablasPorConexion tablasPorConexion = new TablasPorConexion();
				ArrayList<String> tablas = new ArrayList<String>();

				// CREAMOS LA ESTRUCTURA PARA OBTENER LOS CAMPOS DE LA TABLA
				CamposPorTabla camposPorTabla = new CamposPorTabla();
				ArrayList<CampoDTO> campos = new ArrayList<CampoDTO>();
				
			
				boolean fromTable=false;
				
				switch (opcion) {
					case 1:
						from ="tabla";
						// Lógica para la opción 1: Escoger tabla
						fromTable=true;
						// ENVIAMOS LA INFORMACIÓN DE LA CONEXION A LA TABLA ENVIAMOS EL
						// ESCHEMA(USUARIO) Y LA CONEXION EXISTOSA
						tablas = tablasPorConexion.obtenerTablas(conexionOrigen.getUsername(), conOrigen);

						// MOSTRAMOS LA LISTA DE TABLAS QUE EL SCHEMA POSEE
						i = 0;
						System.out.printf("\nLas tablas que el  %s posee son: \n", conexionOrigen.getUsername());
						for (String tabla : tablas) {
							System.out.printf("\t%s. %s\n", i, tabla);
							i++;
						}

						// COLOCAMOS LA VARIABLE DE SELECCION CORRECTA EN FALSE
						seleccionCorrecta = false;

						while (!seleccionCorrecta) {
							System.out.printf("\nSelecciona la tabla de origen : \n");
							tableSelect = input.nextInt();
							// Verificar si el valor ingresado está dentro del rango permitido
							if (tableSelect >= 0 && tableSelect < tablas.size()) {
								seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a
															// true para
															// salir del bucle
							} else {
								System.out.println(
										"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
							}
							// Consumir el salto de línea pendiente después de nextInt() para evitar
							// problemas con las siguientes entradas
							input.nextLine();
						} // WHILE DE INGRESO DE VALOR NUMERICO

						// OBTENEMOS LOS CAMPOS
						campos = camposPorTabla.obtenerCampos(conOrigen, tablas.get(tableSelect));
						break;
					case 2:
						from="consulta";
						// Lógica para la opción 2: Ingresar consulta
						fromTable=false;
						boolean consultaCorrecta = false;

						while (!consultaCorrecta) {
							System.out.println("\nIngrese la consulta para obtener la información:");
							consulta = input.nextLine();

							try {

								
								consultaCorrecta = true;
								campos = camposPorTabla.obtenerCamposConsulta(conOrigen, consulta);
								
								if (campos != null) {
									consultaCorrecta = true;
								} else {
									System.out
											.println("La consulta no produjo resultados válidos. Intente nuevamente.");
								}
								
							} catch (Exception e) {
								System.out.println("Error al ejecutar la consulta: " + e.getMessage());
								consultaCorrecta = false;
							}
						}

						break;

				}// SWITCH DE TABLA O CONSULTA

				// MOSTRAMOS LOS CAMPOS DE LA TABLA O LOS OBTENIDOS CON LA CONSULTA
				
					i = 0;
					System.out.printf("\nLos campos de la "+ from +" seleccionada son : \n");
					for (CampoDTO campo : campos) {
						System.out.printf("\t%s. %-30s  %-15s %d\n", i, campo.getColumnName(), campo.getDataType(),
								campo.getMaxLength());
						i++;
					}
				
				// Arreglo que permite almacenar los campos que el usuario seleccionó, esto es
				// lo que trabajaremos en el data convert
				ArrayList<CampoDTO> camposSelect = new ArrayList<CampoDTO>();

				seleccionCorrecta = false;

				while (!seleccionCorrecta) {

					System.out
							.printf("\nSeleccione los campos que desea obtener, separe los valores con una coma : \n");
					String entrada;
					entrada = input.next();

					String[] valores = entrada.split(",");

					for (String valor : valores) {
						CampoDTO campoDTO = new CampoDTO();
						int index = Integer.parseInt(valor);

							if (index >= 0 && index < campos.size()) {
								campoDTO.setColumnName(campos.get(index).getColumnName());
								campoDTO.setDataType(campos.get(index).getDataType());
								campoDTO.setMaxLength(campos.get(index).getMaxLength());
								campoDTO.setAlias(campos.get(index).getColumnName());
								campoDTO.setColumnNameConvert(campos.get(index).getColumnName());
								campoDTO.setMaxLeghtConvert(campos.get(index).getMaxLength());
								camposSelect.add(campoDTO);

								seleccionCorrecta = true;
							} else {
								seleccionCorrecta = false;
							}
						
						
					}
				} // WHILE DE LISTA DE VALORES DE CAMPOS

				/* -------------------------------------------------------- */
				/* INICIAMOS CON LA CONVERSION DE DATOS */
				/* -------------------------------------------------------- */

				System.out.println("╔═════════════════════════════════════════════════════════════╗");
				System.out.println("║                                                             ║");
				System.out.println("║                    Iniciando la conversión de datos         ║");
				System.out.println("║                                                             ║");
				System.out.println("╚═════════════════════════════════════════════════════════════╝");
				System.out.println("\n\n");

				seleccionCorrecta=false;
				int convertir=0;

				while (!seleccionCorrecta) {
					
			
					System.out.println("╭───────────────────────────╮");
					System.out.println("│                           │");
					System.out.println("│  Opciones disponibles     │");
					System.out.println("│                           │");
					System.out.println("│  1. Transformar           │");
					System.out.println("│  2. Continuar a destino   │");
					System.out.println("│                           │");
					System.out.println("╰───────────────────────────╯");

					convertir = input.nextInt();

					if(convertir==1 || convertir==2){
						seleccionCorrecta=true;
					}
				}
				
				
				if(convertir==1){
					boolean transformar = true;
					int transformarSelect = 1;
					int indexSelect = -1;
					

					while (transformar) {
						seleccionCorrecta = false;

							// MOSTRAMOS A LOS USUARIOS LOS CAMPOS SELECCIONADOS
						i = 0;
						System.out.printf("\nCampos para transformación  \n");
						for (CampoDTO campoConver : camposSelect) {

							System.out.printf("\t%s. %-30s  %-15s %d\n", i, campoConver.getColumnName(),
									campoConver.getDataType(), campoConver.getMaxLength());
							i++;
						}

						transformarSelect = 1;

						while (!seleccionCorrecta) {
							System.out.printf("\nEscoja el campo que desea transformar  \n");
							indexSelect = input.nextInt();
							
							
							if ("VARCHAR2".equals(camposSelect.get(indexSelect).getDataType())
									|| "CHAR".equals(camposSelect.get(indexSelect).getDataType())) {
								System.out.println("╭───────────────────────────╮");
								System.out.println("│                           │");
								System.out.println("│  Opciones disponibles     │");
								System.out.println("│                           │");
								System.out.println("│  1. Convertir a Mayúscula │");
								System.out.println("│  2. Convertir a Minúscula │");
								System.out.println("│  3. Concatenar con campo  │");
								System.out.println("│  4. Concatenar valor      │");
								System.out.println("╰───────────────────────────╯");

							} else if ("TIMESTAMP".equals(camposSelect.get(indexSelect).getDataType())
									|| "DATE".equals(camposSelect.get(indexSelect).getDataType())) {

								System.out.println("╭──────────────────────╮");
								System.out.println("│                      │");
								System.out.println("│  Opciones disponibles│");
								System.out.println("│                      │");
								System.out.println("│  5. Extraer Año      │");
								System.out.println("│  6. Extraer Mes      │");
								System.out.println("│  7. Extraer Día      │");
								System.out.println("│  8. Extraer Hora     │");
								System.out.println("│                      │");
								System.out.println("╰──────────────────────╯");

							}

							opcion = input.nextInt();

							if (opcion > 0 && opcion <= 8) {
								seleccionCorrecta = true;
							} else {
								seleccionCorrecta = false;
							}

							// Limpiar el salto de línea pendiente después de nextInt()
							input.nextLine();

						} // WHILE DE SELECCION CORRECTA

						switch (opcion) {
							case 1:
								// Lógica para la opción 1: Ingresar consulta
								camposSelect.get(indexSelect).upperColumn();
								break;
							case 2:
								// Lógica para la opción 2: Ingresar consulta
								camposSelect.get(indexSelect).lowerColumn();
								break;
							case 3:
								// Lógica para la opción 3: Ingresar consulta
								seleccionCorrecta = false;
								int campoConcat = -1;
								String alias = null;
								CampoDTO campo2 = new CampoDTO();

								// MOSTRAMOS LA LISTA DE CAMPOS CON LOS QUE PUEDE CONCATENAR
								i = 0;
								System.out.printf("\nCampos para concatenar  \n");
								for (CampoDTO campoConver : camposSelect) {

									System.out.printf("\t%s. %-30s  %-15s %d\n", i, campoConver.getColumnName(),
											campoConver.getDataType(), campoConver.getMaxLength());
									i++;
								}

								while (!seleccionCorrecta) {
									System.out.printf("\nIngrese el valor del campo a concatenar  \n");
									campoConcat = input.nextInt();

									if (campoConcat <= 0 || campoConcat < camposSelect.size()) {
										seleccionCorrecta = true;
									} else {
										seleccionCorrecta = false;
									}

									System.out.printf("\nIngrese el valor del alias  \n");
									alias = input.next();
								}
								// copiamos el campo a concatenar
								campo2 = camposSelect.get(campoConcat);
								// llamamos a la función para concatenar
								camposSelect.get(indexSelect).concatValues(campo2, alias);
								

								break;
							case 4:
								// Lógica para la opción 4: Escoger tabla

								String valor = null;
								String aliasV = null;
								System.out.printf("\nIngrese el valor a concatenar  \n");
								valor = input.nextLine();

								System.out.printf("\nIngrese el valor del alias  \n");
								aliasV = input.next();

								camposSelect.get(indexSelect).concatValues(valor, aliasV);
								break;
							case 5:
								// Lógica para la opción 5: Ingresar consulta
								String aliasB = null;
								System.out.printf("\nEl valor devuelto será number \n");
								System.out.printf("\nIngrese el valor del alias  \n");
								aliasB = input.next();
								
								camposSelect.get(indexSelect).Extraer("YEAR", aliasB);
								camposSelect.get(indexSelect).setDataType("NUMBER");
								break;
							case 6:
								// Lógica para la opción 6: Ingresar consulta
								String aliasC = null;
								System.out.printf("\nEl valor devuelto será number \n");
								System.out.printf("\nIngrese el valor del alias  \n");
								aliasC = input.next();
								
								camposSelect.get(indexSelect).Extraer("MONTH", aliasC);
								camposSelect.get(indexSelect).setDataType("NUMBER");
								break;
							case 7:
								// Lógica para la opción 7: Ingresar consulta
								String aliasD = null;
								System.out.printf("\nEl valor devuelto será number \n");
								System.out.printf("\nIngrese el valor del alias  \n");
								aliasD = input.next();
								camposSelect.get(indexSelect).Extraer("DAY", aliasD);
								camposSelect.get(indexSelect).setDataType("NUMBER");
								break;
							case 8:
								// Lógica para la opción 8: Ingresar consulta
								System.out.printf("\nEl valor devuelto será VARCHAR2 \n");
								String aliasE = null;
								System.out.printf("\nIngrese el valor del alias  \n");
								aliasE = input.next();
								camposSelect.get(indexSelect).Extraer("HOUR",aliasE);
								camposSelect.get(indexSelect).setDataType("VARCHAR2");
								break;

						}

						System.out.println("╭───────────────────────╮");
						System.out.println("│                       │");
						System.out.println("│  Opciones disponibles │");
						System.out.println("│                       │");
						System.out.println("│  1. Seguir transformando│");
						System.out.println("│  2. Salir             │");
						System.out.println("╰───────────────────────╯");

						seleccionCorrecta = false;

						while (!seleccionCorrecta) {
							transformarSelect = input.nextInt();

							if (transformarSelect == 1) {
								transformar = true;
								seleccionCorrecta = true;

							} else if (transformarSelect == 2) {
								transformar = false;
								seleccionCorrecta = true;
							} else {
								seleccionCorrecta = false;
							}
						}

					} // WHILE DE TRANSFORMACIÓN
				}//IF DE CONVERSIÓN

				System.out.println("╔═════════════════════════════════════════════════════════════╗");
				System.out.println("║                                                             ║");
				System.out.println("║                    Escogiendo el destino de los datos       ║");
				System.out.println("║                                                             ║");
				System.out.println("╚═════════════════════════════════════════════════════════════╝");
				System.out.println("\n\n");

				// conexiones = conexionesDisponibles.obtenerConexionesDisponibles();
				System.out.println(
						"\nA continuación se muestran las conexiones disponibles :");

				// RECORREMOS LA ESTRCUTURA Y LA MOSTRAMOS AL USUARIO
				i = 0;
				for (ConexionDTO conexionDTO : conexiones) {
					System.out.printf("\t%s. %s\n", i, conexionDTO.getUsername());
					i++;
				}

				// USUARIO SELECCIONA LA CONEXION
				// VERIFICAR QUE EL VALOR INGRESADO ESTE DENTRO DEL RANGO PERMITIDO
				int conexionSelec2 = 0;
				seleccionCorrecta = false;

				while (!seleccionCorrecta) {
					System.out.println("\nFavor seleccione una conexión como destino de los datos:");
					conexionSelec2 = input.nextInt();

					// Verificar si el valor ingresado está dentro del rango permitido
					if (conexionSelec2 >= 0 && conexionSelec2 < conexiones.size()) {
						seleccionCorrecta = true; // Si está dentro del rango, establecer seleccionCorrecta a true para
													// salir del bucle
					} else {
						System.out.println(
								"El valor ingresado no es válido. Por favor, seleccione un número dentro del rango permitido.");
					}

					// Consumir el salto de línea pendiente después de nextInt() para evitar
					// problemas con las siguientes entradas
					input.nextLine();
				} // WHILE DE INGRESO DE VALOR NUMERICO

				credencialesCorrectas = false;
				ConexionDTO conexionDestino = conexiones.get(conexionSelec2);
				String contraseniaDestino = null;
				Conexion conexion2 = new Conexion();
				Connection conDestino = null;

				while (!credencialesCorrectas) {
					// Solicitamos las credenciales de la conexión
					conexionOrigen = conexiones.get(conexionSelec2);
					System.out.printf("Escriba la contraseña del usuario %s: ", conexionDestino.getUsername());
					contraseniaDestino = input.next();

					// Intentamos establecer la conexión

					try {
						conDestino = conexion2.openConnection(conexionDestino.getUsername(), contraseniaDestino);
						credencialesCorrectas = true; // Si la conexión es exitosa, establecemos la bandera a true para
														// salir del bucle

					} catch (SQLException e) {
						System.out.println("No se pudo establecer la conexión. Verifique las credenciales.");
						credencialesCorrectas = false;

					}

				} // WHILE DE INTENTO DE CONEXION

				// CREAMOS UNA NUEVA ESTRUCTURA PARA MOSTRAR LAS TABLAS DE LA CONEXIÓN DE
				// DESTINO
				TablasPorConexion tablasPorConexionDes = new TablasPorConexion();
				ArrayList<String> tablasDes = new ArrayList<String>();
				tablasDes = tablasPorConexionDes.obtenerTablas(conexionDestino.getUsername(), conDestino);

				i = 0;
				System.out.printf("\nVer tablas de la conexión %s: \n", conexionDestino.getUsername());
				for (String tablaDes : tablasDes) {
					System.out.printf("\t%s. %s\n", i, tablaDes);
					i++;
				}

				seleccionCorrecta = false;
				int tableSelectDes = -1;

				while (!seleccionCorrecta) {
				System.out.printf("\nSelecciona una tabla como destino: \n");
					tableSelectDes = input.nextInt();

					if (tableSelectDes >= 0 && tableSelectDes < tablasDes.size()) {
						seleccionCorrecta = true;
					} else {
						seleccionCorrecta = false;
					}

					input.nextLine(); // Consumir el carácter de nueva línea en el búfer
				}

				// CREAMOS LA ESTRUCTURA PARA OBTENER LOS CAMPOS DE LA TABLA DE DESTINO
				CamposPorTabla camposPorTablaDes = new CamposPorTabla();
				ArrayList<CampoDTO> camposDes = new ArrayList<CampoDTO>();
				camposDes = camposPorTablaDes.obtenerCampos(conDestino, tablasDes.get(tableSelectDes));

				// IMPRIMIMOS LOS CAMPOS TANTO DE LA TABLA DE ORIGEN COMO DESTINO PARA
				// ORDENARLOS
			

				i = 0;
				System.out.printf("\nLos campos de la tabla destino %s son : \n", tablasDes.get(tableSelectDes));
				for (CampoDTO campoDes : camposDes) {

					System.out.printf("\t%s. %-30s  %-15s %d \n", i, campoDes.getColumnName(), campoDes.getDataType(),
							campoDes.getMaxLength());
					i++;
				}

				
				String orden;
				seleccionCorrecta=false;
				String[] valoresOrden=null;
				String[] valoresOrdenDes=null;
				while (!seleccionCorrecta) {
					System.out.printf(
						"\nIngrese el valor de los campos de destino que desea llenar en orden , separe con comas: \n");

					orden = input.nextLine();
					valoresOrdenDes = orden.split(",");

					for(String valor: valoresOrdenDes){
						int	index = Integer.parseInt(valor);

						
						if(index>=0 && index<camposDes.size()){
							seleccionCorrecta=true;
						}else{
							seleccionCorrecta=false;
						}

					}

				}

				//aqui creamos el orden de los campos de destino
				ArrayList<String> camposSelectDestino = new ArrayList<String>();
				

				for (int j = 0; j < valoresOrdenDes.length; j++) {
					int index = Integer.parseInt(valoresOrdenDes[j]);

					camposSelectDestino.add(camposDes.get(index).getColumnNameConvert());
					
				}

				//MOSTRAMOS LOS CAMPOS DEL DESTINO YA SELECCIONADOS
				i = 0;
				System.out.printf("\nLos campos de la tabla destino %s a llenar son : \n", tablasDes.get(tableSelectDes));
				for (String valor: valoresOrdenDes) {
					int	index = Integer.parseInt(valor);
					System.out.printf("\t%s. %-30s  %-15s %d \n", i, camposDes.get(index).getColumnName(), camposDes.get(index).getDataType(),
					camposDes.get(index).getMaxLength());
					i++;
				}

				//MOSTRAMOS LOS CAMPOS DE ORIGEN YA TRANSFORMADOS
				i = 0;
				System.out.printf("\nLos campos de origen  son : \n");
				for (CampoDTO campoSelect : camposSelect) {
					System.out.printf("\t%s. %-30s  %-15s  %d  \n", i, campoSelect.getAlias(),
							campoSelect.getDataType(), campoSelect.getMaxLeghtConvert());
					i++;
				}		
			
				
				
				seleccionCorrecta=false;

						while (!seleccionCorrecta) {
							System.out.printf(
						  "\nIngrese el valor de los campos de origen en orden en que desea llenar los campos de destino , separe con comas: \n");
		
							orden = input.nextLine();
							valoresOrden = orden.split(",");
		
							for(String valor: valoresOrden){
								int	index = Integer.parseInt(valor);
		
								if(index>=0 && index<camposSelect.size()){ //los valores tienen que estar dentro de la longitud de los campos convertidos
									seleccionCorrecta=true;
								}else{
									seleccionCorrecta=false;
								}
		
							}
		
						}


				ArrayList<String> camposSelectOrden = new ArrayList<String>();
				//ArrayList<CampoDTO> camposOrden = new ArrayList<CampoDTO>();

				for (int j = 0; j < valoresOrden.length; j++) {
					int index = Integer.parseInt(valoresOrden[j]);

					camposSelectOrden.add(camposSelect.get(index).getColumnNameConvert());
					
				}

				// Tengo el orden ahora necesito ingresarlos en la tabla
				IngresarDatosDestino ingresarDatosDestino = new IngresarDatosDestino();

				//ALTERAMOS LOS CAMPOS EN LA TABLA DESTINO QUE NECESITAN CAMBIAR SU LONGITUD
				for(int k=0;k<camposSelectDestino.size();k++){
					//obtenemos el primer indice en orden
					int select =Integer.parseInt(valoresOrden[k]);
					int SelectDes = Integer.parseInt(valoresOrdenDes[k]);
					// Obtenemos la logitudes maximas de origen y destino
					int maxLeghtOrigen= camposSelect.get(select).getMaxLeghtConvert(); 
					int maxLeghtDestino=camposDes.get(SelectDes).getMaxLength();
					
					if(maxLeghtOrigen>maxLeghtDestino){
						String cambio = camposDes.get(SelectDes).getDataType()+"("+maxLeghtOrigen+")";
						ingresarDatosDestino.alterMaxLength(conDestino,tablasDes.get(tableSelectDes) , camposDes.get(SelectDes).getColumnName(), cambio);
					}
				}

				
				if(fromTable){
					
					int cantInsert = ingresarDatosDestino.ingresarRegistros(conOrigen, conDestino,camposSelectDestino , camposSelectOrden,
							tablas.get(tableSelect), tablasDes.get(tableSelectDes),fromTable );
					System.out.printf("La cantidad de registros ingresados fueron : %d\n", cantInsert);
				}else if(!fromTable){
					//si no es una tabla volvemos a armar la consulta para obtener los datos ahora transformados
					int cantInsert = ingresarDatosDestino.ingresarRegistros(conOrigen, conDestino, camposSelectDestino, camposSelectOrden,
					"temp_table", tablasDes.get(tableSelectDes), fromTable);
					System.out.printf("La cantidad de registros ingresados fueron : %d\n", cantInsert);	
				}
				

			

				// CERRAR CONEXION DE ORIGEN y DESTINO
				conexion.closeConnection(conOrigen);
				conexion2.closeConnection(conDestino);

				System.out.println("╭───────────────────╮");
				System.out.println("│                   │");
				System.out.println("│  Menú de opciones │");
				System.out.println("│                   │");
				System.out.println("│  1. Seguir Creando│");
				System.out.println("│  2. Salir         │");
				System.out.println("╰───────────────────╯");

				seleccionCorrecta = false;
				int opcionEtl=0;
					while (!seleccionCorrecta) {
						opcionEtl = input.nextInt();

						if (opcionEtl == 1) {
							crear = true;
							seleccionCorrecta = true;

						} else if (opcionEtl == 2) {
							crear = false;
							seleccionCorrecta = true;
						} else {
							seleccionCorrecta = false;
						}
					}

			} 

		}

	}

}
