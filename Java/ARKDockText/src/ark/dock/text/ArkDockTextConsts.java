package ark.dock.text;

import java.util.Map;

import ark.dock.ArkDockConsts;

public interface ArkDockTextConsts extends ArkDockConsts {
	
    interface EditorConfigValues {
        String SECTION = "section";
        String KEY = "key";
        String VALUE = "value";
    }

	interface StringConverter {
		<TargetType> TargetType fromStr(String str);
		String toString(Object ob);
	}
	
	public interface TextMatchListener {
		void processMatch(int idx, Map<String, String> content);
	}

}
