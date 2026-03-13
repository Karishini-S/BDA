import pandas as pd
from sklearn.preprocessing import StandardScaler

class MetadataPipeline:
    def __init__(self):
        self.scaler = StandardScaler()

    def extract_features(self, df):
        features = pd.DataFrame()

        # Rating (renamed earlier from rating → overall)
        features["overall"] = df["overall"]

        # Review length from text
        features["review_length"] = df["reviewText"].fillna("").apply(len)

        # Helpful votes
        features["helpful_vote"] = df["helpful_vote"]

        # Verified purchase → convert bool to int
        features["verified_purchase"] = df["verified_purchase"].astype(int)

        return features.values

    def fit_transform(self, features):
        return self.scaler.fit_transform(features)

    def transform(self, features):
        return self.scaler.transform(features)
