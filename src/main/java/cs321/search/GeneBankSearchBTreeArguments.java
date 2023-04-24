package cs321.search;

public class GeneBankSearchBTreeArguments
{
    private final boolean useCache;
    private final int degree;
    private final String btreeFileName;
    private final int subsequenceLength;
    private final String queryFileName;
    private final int cacheSize;
    private final int debugLevel;

    public GeneBankSearchBTreeArguments(boolean useCache, int degree, String btreeFileName, int subsequenceLength, String queryFileName, int cacheSize, int debugLevel)
    {
        this.useCache = useCache;
        this.degree = degree;
        this.btreeFileName = btreeFileName;
        this.subsequenceLength = subsequenceLength;
        this.queryFileName = queryFileName;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        GeneBankSearchBTreeArguments other = (GeneBankSearchBTreeArguments) obj;
        if (cacheSize != other.cacheSize)
        {
            return false;
        }
        if (debugLevel != other.debugLevel)
        {
            return false;
        }
        if (degree != other.degree)
        {
            return false;
        }
        if (btreeFileName == null)
        {
            if (other.btreeFileName != null)
            {
                return false;
            }
        }
        else
        {
            if (!btreeFileName.equals(other.btreeFileName))
            {
                return false;
            }
        }
        if (queryFileName == null)
        {
            if (other.queryFileName != null)
            {
                return false;
            }
        }
        else
        {
            if (!queryFileName.equals(other.queryFileName))
            {
                return false;
            }
        }
        if (subsequenceLength != other.subsequenceLength)
        {
            return false;
        }
        if (useCache != other.useCache)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        //this method was generated using an IDE
        return "GeneBankCreateBTreeArguments{" +
                "useCache=" + useCache +
                ", degree=" + degree +
                ", btreeFileName='" + btreeFileName + '\'' +
                ", subsequenceLength=" + subsequenceLength +
                ", queryFileName='" + queryFileName + '\'' +
                ", cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                '}';
    }

}
