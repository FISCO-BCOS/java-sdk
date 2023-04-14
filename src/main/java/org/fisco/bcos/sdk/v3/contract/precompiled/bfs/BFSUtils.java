package org.fisco.bcos.sdk.v3.contract.precompiled.bfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;

public class BFSUtils {
    public static final String BFS_TYPE_DIR = "directory";
    public static final String BFS_TYPE_CON = "contract";
    public static final String BFS_TYPE_LNK = "link";

    public static final String BFS_ROOT = "/";
    public static final String BFS_APPS = "/apps";
    public static final String BFS_SYS = "/sys";
    public static final String BFS_TABLES = "/tables";
    public static final String BFS_USER = "/usr";

    public static final Set<String> BFS_SYSTEM_PATH =
            new HashSet<>(Arrays.asList(BFS_ROOT, BFS_APPS, BFS_SYS, BFS_TABLES, BFS_USER));

    private BFSUtils() {}

    public static Tuple2<String, String> getParentPathAndBaseName(String path) {
        if (path.equals("/")) return new Tuple2<>("/", "/");
        List<String> path2Level = path2Level(path);
        String baseName = path2Level.get(path2Level.size() - 1);
        String parentPath = '/' + String.join("/", path2Level.subList(0, path2Level.size() - 1));
        return new Tuple2<>(parentPath, baseName);
    }

    public static List<String> path2Level(String absolutePath) {
        Stack<String> pathStack = new Stack<>();
        for (String s : absolutePath.split("/")) {
            if (s.isEmpty() || s.equals(".")) {
                continue;
            }
            if (s.equals("..")) {
                if (!pathStack.isEmpty()) {
                    pathStack.pop();
                }
                continue;
            }
            pathStack.push(s);
        }
        return new ArrayList<>(pathStack);
    }
}
