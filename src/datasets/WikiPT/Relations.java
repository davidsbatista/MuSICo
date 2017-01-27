package datasets.WikiPT;

import java.io.*;
import java.util.*;

/**
 * this code was used to generate file with the aggregated relations from the Portuguese DBpedia
 * available at: http://dmir.inesc-id.pt/project/DBpediaRelations-PT_01_in_English
 **/

public class Relations {

	public static Map<String, String> aggregatedRelations = null; 		
	
	public static String[] ignore = {"picture", "maintainedBy", "politicGovernmentDepartment", "regionServed",
			 					     "leader","owner","city"};
	
	public static String[] changeDirection = {"predecessor","doctoralAdvisor","influenced","child","foundedBy",
											  "capitalCountry","currentMember","pastMember","keyPerson","president",
                                              "leaderName", "successor","city","capital"};
	
	public static String other[] = {"number","relative","crosses","neighboringMunicipality","sisterStation",
                                    "portrayer","patron","knownFor","ground","tenant","appearancesInNationalTeam",
                                    "riverMouth",};
	
	public static void initialize() {
		aggregatedRelations = new HashMap<String, String>();
		
		//trocar a direccao para encaixar em "locatedinArea"
		//capitalCountry
		//*city
		//*capital
		String locatedInArea[] = {"locatedInArea","archipelago","location", "locationCity",
								  "locationCountry","municipality","subregion","federalState","district","region",
								  "province","state","county", "map","campus","garrison","department","country"};		
		
		//trocar a direccao para encaixar em "partOf"
		//currentMember
		//pastMember
		String partOf[] = {"type","parentOrganisation","distributingCompany","broadcastNetwork","affiliation",
						   "university","youthClub","party","pastMember","team","associatedMusicalArtist","member"};						
		
		String origin[] = {"origin","birthPlace","foundationPlace","sourcePlace","headquarter","nationality","residence","hometown","sportCountry"};						
		String deathOrBurialPlace[] = {"deathPlace","placeOfBurial"};		
		String partner[] = {"spouse","partner"};

		//trocar direcÃ§Ã£o para encaixar em "e1, keyperson in e2"
		//keyPerson
		//president
		//leaderName
		String keyPerson[] = {"keyPerson","president","monarch","leader","leaderName","founder"};		
		
		String influencedBy[] = {"influencedBy"};
		
		String parent[] = {"parent"};
		
		//trocar direcção para encaixar em "e1, succesor of e2"
		String successor[] = {"successor"};	
		
		addRelations(locatedInArea,"locatedInArea");
		addRelations(partOf,"partOf");
		addRelations(origin,"origin");		
		addRelations(deathOrBurialPlace,"deathOrBurialPlace");
		addRelations(partner,"partner");
		addRelations(keyPerson,"keyPerson");
		addRelations(influencedBy,"influencedBy");
		addRelations(parent,"parent");
		addRelations(successor,"successor");
		addRelations(other,"other");
		
		Set<String> relations = new HashSet<String>();		
		for (String relation : aggregatedRelations.keySet()) {			
			relations.add(aggregatedRelations.get(relation));
		}
	}
	
	public static void addRelations(String[] lowerRelations, String relationName) {
			for (String r : lowerRelations) {
				aggregatedRelations.put(r, relationName);
			}
		}

    public static void writeRelationsFile(String inputFile) throws IOException {
        initialize();
        PrintWriter out = new PrintWriter(new FileWriter("data-wikipt.txt"));
        BufferedReader input = new BufferedReader(new FileReader(inputFile));
        String aux;
        String type;
        String e1 = null;
        String e2 = null;
        while ((aux = input.readLine()) != null) {
            if (!aux.startsWith("REL TYPE"))
                out.write(aux + "\n");
            else if (aux.startsWith("REL TYPE")) {
                type = aux.split(": ")[1];
                if (Arrays.asList(ignore).contains(type)) type="other";
                else {
                    if (!Arrays.asList(changeDirection).contains(type)) {
                        // transform relationship type into a top aggregated type
                        type  = aggregatedRelations.get(type);
                    }
                    else {
                        // keyPerson, president, leaderName to keyPerson
                        if (type.equals("keyPerson") || type.equals("president") || type.equals("leaderName")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "keyPerson";
                        }
                        // currentMember and pastMember to partOf
                        if (type.equals("currentMember") || type.equals("pastMember")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "partOf";
                        }
                        // capitalCountry to locatedinArea
                        if (type.equals("capitalCountry") || type.equals("city") || type.equals("capital")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "locatedInArea";
                        }
                        // more examples of "influencedBy"
                        if (type.equals("influenced") || type.equals("doctoralAdvisor")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "influencedBy";
                        }
                        // more examples of "successor"
                        if (type.equals("predecessor") || type.equals("successor")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "successor";
                        }
                        // more examples of "parent"
                        if (type.equals("child")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "parent";
                        }
                        // more examples of "keyPerson"
                        if (type.equals("foundedBy")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "keyPerson";
                        }
                        //largestCity to locatedInArea
                        if (type.equals("largestCity")) {
                            String tmp = e2;
                            e2 = e1;
                            e1 = tmp;
                            type = "locatedInArea";
                        }
                    }
                }
                out.write("REL TYPE : " + type +'\n');
            }
        }
    }
}