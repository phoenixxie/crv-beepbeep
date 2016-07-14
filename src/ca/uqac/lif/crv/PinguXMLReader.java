package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Processor;
import ca.uqac.lif.cep.SingleProcessor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

public class PinguXMLReader extends SingleProcessor {
    private static XMLInputFactory inputFactory = XMLInputFactory.newInstance();

    String filename;
    XMLStreamReader streamReader = null;

    public PinguXMLReader(String filename) {
        super(0, 1);

        this.filename = filename;
    }

    @Override
    protected Queue<Object[]> compute(Object[] objects) {
        if (streamReader == null) {
            InputStream in = null;
            try {
                in = new FileInputStream(filename);
                streamReader = inputFactory.createXMLStreamReader(in);
            } catch (FileNotFoundException | XMLStreamException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        PinguTrace trace = null;
        PinguTrace.Character character = null;
        PinguTrace.Vector2D vector2D = null;

        try {
            streamReader.next();

            while (streamReader.hasNext()) {
                if (streamReader.isStartElement()) {
                    String localName = streamReader.getLocalName();
                    switch (localName) {
                        case "message":
                            trace = new PinguTrace();
                            break;
                        case "timestamp":
                            trace.setTimestamp(Long.parseLong(streamReader.getElementText()));
                            break;
                        case "characters":
                            break;
                        case "character":
                            character = new PinguTrace.Character();
                            break;
                        case "id":
                            character.id = Integer.parseInt(streamReader.getElementText());
                            break;
                        case "status":
                            character.status = PinguTrace.parseFromString(streamReader.getElementText());
                            break;
                        case "position":
                            vector2D = new PinguTrace.Vector2D();
                            break;
                        case "velocity":
                            vector2D = new PinguTrace.Vector2D();
                            break;
                        case "x":
                            vector2D.x = Float.parseFloat(streamReader.getElementText());
                            break;
                        case "y":
                            vector2D.y = Float.parseFloat(streamReader.getElementText());
                            break;
                    }

                } else if (streamReader.isEndElement()) {
                    String localName = streamReader.getLocalName();
                    switch (localName) {
                        case "message":
                            return wrapObject(trace);
                        case "characters":
                            break;
                        case "character":
                            trace.addCharacter(character);
                            break;
                        case "position":
                            character.position = vector2D;
                            break;
                        case "velocity":
                            character.velocity = vector2D;
                            break;
                    }
                }
                streamReader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
            try {
                streamReader.close();
            } catch (XMLStreamException ee) {
                ee.printStackTrace();
            }

            return null;
        }

        try {
            streamReader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    @Override
    public Processor clone() {
        System.err.println("Cannot be cloned!");
        System.exit(1);

        return null;
    }
}
