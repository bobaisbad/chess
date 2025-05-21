package result;

import model.GameInfo;
import java.util.Collection;

public record ListResult(Collection<GameInfo> games) {}
